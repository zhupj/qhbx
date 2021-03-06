/*
 * Copyright (c) 2011 Ruaho All rights reserved.
 */
package com.rh.core.serv;

import java.io.BufferedInputStream;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.rh.core.base.Bean;
import com.rh.core.base.BeanUtils;
import com.rh.core.base.Context;
import com.rh.core.base.TipException;
import com.rh.core.base.db.QueryCallback;
import com.rh.core.base.db.Transaction;
import com.rh.core.comm.FileMgr;
import com.rh.core.comm.entity.EntityMgr;
import com.rh.core.comm.mind.MindLabelContentProvider;
import com.rh.core.serv.ServUtils.SERV_PARAM;
import com.rh.core.serv.base.AbstractServ;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.serv.dict.DictMgr;
import com.rh.core.serv.relate.RelateMgr;
import com.rh.core.util.Constant;
import com.rh.core.util.JsonUtils;
import com.rh.core.util.Lang;
import com.rh.core.util.RequestUtils;
import com.rh.core.util.Strings;
import com.rh.core.util.file.FileHelper;
import com.rh.core.wfe.serv.ProcDefServ;

/**
 * 公共的服务响应类
 * 级联处理标志ParamBean.setLinkFlag(true)：       byid、finds、delete如果有级联标志会自动向下级联处理； 
 *                                          save、batchSave、add不需要级联删除标志，自动根据数据内容处理。
 * @author Jerry Li
 */
public class CommonServ extends AbstractServ {
    
	/**每次获取数据条数*/
	private static final int ONETIME_EXP_NUM = 5000;
	/**excel最大行数*/
	private static final int EXCEL_MAX_NUM = 65536;
	
    /**
     * 提供基于列表的查询服务
     * @param paramBean    参数Bean
     * @return 查询结果
     */
    public OutBean query(ParamBean paramBean) {
        beforeQuery(paramBean);
        final ServDefBean serv = ServUtils.getServDef(paramBean.getServId());
        Bean page = paramBean.getQueryPage();
        int rowCount = paramBean.getShowNum(); //通用分页参数优先级最高，然后是查询的分页参数
        if (rowCount > 0) { //快捷参数指定的分页信息，与finds方法兼容
            page.set("SHOWNUM", rowCount); //从参数中获取需要取多少条记录，如果没有则取所有记录
            page.set("NOWPAGE", paramBean.getNowPage());  //从参数中获取第几页，缺省为第1页
        } else {
            if (!page.contains("SHOWNUM")) { //初始化每页记录数设定
                page.set("SHOWNUM", serv.getPageCount(50));
            }
            if (!page.contains("NOWPAGE")) { //初始化当前页数设定
                page.set("NOWPAGE", 1); 
            }
        }
        
        OutBean outBean = new OutBean();
        final LinkedHashMap<String, Bean> cols = new LinkedHashMap<String, Bean>();
        StringBuilder sql = new StringBuilder("select ");
        
        //在查询语句Select之后加入指定关键字，如：Distinct
        if (paramBean.isNotEmpty(Constant.SELECT_KEYWORDS)) {
            sql.append(" ").append(paramBean.getStr(Constant.SELECT_KEYWORDS)).append(" ");
        }
        
        String qSelect = paramBean.getSelect();
        final String[] hideItems;
        if (qSelect.length() == 0 || qSelect.equals("*")) {
            boolean bListFlag = (qSelect.equals("*")) ? true : false;
            LinkedHashMap<String, Bean> items = serv.getAllItems();
            StringBuilder select = new StringBuilder(serv.getPKey());
            boolean bKey = true;
            for (String key : items.keySet()) {
                Bean item = items.get(key);
                int listFlag = item.getInt("ITEM_LIST_FLAG");
                if (bKey && item.getStr("ITEM_CODE").equals(serv.getPKey())) { //主键无论是否列表显示都输出
                    if (listFlag == Constant.ITEM_LIST_FLAG_HIDDEN) { //如果定义为隐藏有数据，则提供给前端时设为不显示
                        listFlag = Constant.ITEM_LIST_FLAG_NO;
                    }
                    addCols(cols, item, listFlag);
                    bKey = false;
                } else if (listFlag != Constant.ITEM_LIST_FLAG_NO || bListFlag) {
                    if (item.getInt("ITEM_TYPE") == Constant.ITEM_TYPE_TABLE 
                            || item.getInt("ITEM_TYPE") == Constant.ITEM_TYPE_VIEW) {
                        select.append(",").append(item.get("ITEM_CODE"));
                    }
                    if (listFlag == Constant.ITEM_LIST_FLAG_HIDDEN) { //如果定义为隐藏有数据，则提供给前端时设为不显示
                        listFlag = Constant.ITEM_LIST_FLAG_NO;
                    }
                    addCols(cols, item, listFlag);
                }
            } //end for
            sql.append(select);
            hideItems = new String[] {};
        } else { //自定义select的列查询后再处理
            sql.append(qSelect);
            hideItems = paramBean.getQueryHide().split(Constant.SEPARATOR);
        }

        //处理Order排序
        StringBuilder order = new StringBuilder(" order by ");
        String servOrder = serv.getSqlOrder();
        String pageOrder = paramBean.getQueryPageOrder();
        if (pageOrder.length() > 0) { //排序设定的优先级：分页排序 > 参数排序 > 服务排序 > 更新时间倒叙 
            order.append(pageOrder);
        } else {
            if (paramBean.contains(Constant.PARAM_ORDER)) {
                order.append(paramBean.getOrder());
            } else if (servOrder.length() > 0) { //存在缺省排序设定
                order.append(servOrder);
            } else { //没有任何排序设定
                order.setLength(0); //清除排序
            }
        }
        sql.append(" from ").append(serv.getTableView()).append(" where 1=1 ")
            .append(getFullWhere(paramBean));
        if (StringUtils.isNotEmpty(paramBean.getGroupBy())) {
            sql.append(" GROUP BY ").append(paramBean.getGroupBy());
        }
        sql.append(order);
        List<Bean> dataList = Transaction.getExecutor().queryPage(
            sql.toString(), page.getInt("NOWPAGE"), page.getInt("SHOWNUM"),
            new QueryCallback() { //处理数据字典的名称替换
                public void call(List<Bean> columns, Bean bean) {
                    String cmpy = null;
                    String cmpyItem = serv.getCmpy();
                    if (cmpyItem.length() > 0 && bean.isNotEmpty(cmpyItem)) { //指定当前数据的公司编码
                        cmpy = Context.changeCmpy(bean.getStr(cmpyItem));
                    }
                    for (Bean column : columns) {
                        Bean item = serv.getItem(column.get("NAME"));
                        if (item != null) {
                            if (item.getInt("ITEM_USER_FLAG") == Constant.YES_INT) { //处理用户的各种信息
                                String itemCode = item.getStr("ITEM_CODE");
                                appendUserItemInfo(itemCode, bean);
                            } else  if (item.isNotEmpty("DICT_ID")) { //数据字典项
                            		String dictId = item.getStr("DICT_ID");
		            	           	if (dictId.startsWith("@")) {
		            	        			ParamBean param = null;
		            	        			String config = item.getStr("ITEM_INPUT_CONFIG");
                                        Bean configBean = JsonUtils.toBean(config.substring(config
                                                .indexOf(Constant.SEPARATOR) + 1));
		            	        			param = new ParamBean(configBean.getBean("params"));
		            	        			dictId = DictMgr.getDictId(dictId.substring(1), param);
		            	           	}
		            	           	String data = bean.getStr(column.getStr("NAME"));
		            	           	bean.set(column.getStr("NAME") + "__NAME", DictMgr.getFullNames(dictId, data));
		            	           	if (item.getInt("ITEM_MOBILE_TYPE") == Constant.ITEM_MOBILE_TYPE_IMG) { //图片项
		            	           		String img = DictMgr.getImg(dictId, data);
		            	           		bean.set(column.getStr("NAME") + "__IMG", img);
		            	           	}
                            }
                        }
                    }
                    if (cmpy != null) {
                        Context.changeCmpy(cmpy);
                    }
                }
                
                public void end(int count, List<Bean> columns) { //自定义select需要根据查询结果生成列信息
                    if (cols.size() == 0) {
                        for (Bean col : columns) {
                            String name = col.getStr("NAME");
                            Bean item = serv.getItem(name);
                            if (item == null) {
                                continue;
                            }
                            //前端指定了隐藏字段的不显示, 服务定义指定了隐藏有数据字段不显示
                            if (Strings.isin(hideItems, name)
                                    || (item.getInt("ITEM_LIST_FLAG") == Constant.ITEM_LIST_FLAG_HIDDEN)) {
                                addCols(cols, item, Constant.NO_INT);
                            } else {
                                addCols(cols, item, Constant.YES_INT);
                            }
                        }
                    } //end if
                }
            });
        int count = dataList.size();
        int showCount = page.getInt("SHOWNUM");
        boolean bCount; //是否计算分页
        if ((showCount == 0) || serv.noCount() || paramBean.getQueryNoPageFlag()) {
            bCount = false;
        } else {
            bCount = true;
        }
        if (bCount) { //进行分页处理
            if (!page.contains("ALLNUM")) { //如果有总记录数就不再计算
                if ((page.getInt("NOWPAGE") == 1) && (count < showCount)) { //数据量少，无需计算分页
                    page.set("PAGES", 1);
                    page.set("ALLNUM", count);
                } else {
                    int allNum = Transaction.getExecutor().count(sql.toString());
                    int pages = 0;
                    if (allNum > 0) {
                    	if (allNum % page.getInt("SHOWNUM") == 0) {
                    		pages = allNum / page.getInt("SHOWNUM");
                    	} else {
                    		pages = allNum / page.getInt("SHOWNUM") + 1;
                    	}
                    }
                    page.set("PAGES", pages);
                    page.set("ALLNUM", allNum);
                }
            }
            outBean.setCount(page.getInt("ALLNUM")); //设置为总记录数
        } else {
            outBean.setCount(dataList.size());
        }
        
        outBean.setData(dataList);
        outBean.setPage(page);
        outBean.setCols(cols);
        afterQuery(paramBean, outBean);
        return outBean;
    }
    
    @Override
	protected void beforeDelete(ParamBean paramBean) {
		// TODO Auto-generated method stub
		//super.beforeDelete(paramBean);
    	String  Relatedservices = Context.getSyConf("OA_RELATED_SERVICES", "");
    	String servid = paramBean.getServId();
    	String gw_id = paramBean.getStr("_PK_");
    	System.out.println("servid = " + servid);    
    	
    	if(Relatedservices.indexOf(servid) >= 0){
    		//查询当前文件是否被引用
    		SqlBean sql = new SqlBean();
            sql.and("RELATE_DATA_ID", gw_id);
            Bean bean = ServDao.find("SY_SERV_RELATE", sql);
            if(bean !=null){
        		SqlBean sqlselect = new SqlBean();
        		sqlselect.and("DATA_ID", bean.getStr("DATA_ID"));
                Bean beanselect = ServDao.find("SY_COMM_ENTITY", sqlselect);
                String outstr = "此文件与文件编号:"+beanselect.getStr("ENTITY_CODE")+"标题为:"+beanselect.getStr("TITLE")+"的文件关联! 无法删除";
            	//throw new TipException("wwww");
            }  		
    	}      	
	}

	@Override
	public OutBean serv(ParamBean paramBean) {
		// TODO Auto-generated method stub
		return super.serv(paramBean);
	}

	@Override
	protected void checkActExpression(ServDefBean servDef, String act,
			Bean dataBean) {
		// TODO Auto-generated method stub
		super.checkActExpression(servDef, act, dataBean);
	}

	@Override
	protected void setMsg(Bean bean, String msg) {
		// TODO Auto-generated method stub
		super.setMsg(bean, msg);
	}

	@Override
	protected void setOk(Bean bean) {
		// TODO Auto-generated method stub
		super.setOk(bean);
	}

	@Override
	protected void setOk(Bean bean, String msg) {
		// TODO Auto-generated method stub
		super.setOk(bean, msg);
	}

	@Override
	protected void setWarn(Bean bean) {
		// TODO Auto-generated method stub
		super.setWarn(bean);
	}

	@Override
	protected void setWarn(Bean bean, String msg) {
		// TODO Auto-generated method stub
		super.setWarn(bean, msg);
	}

	@Override
	protected void setError(Bean bean) {
		// TODO Auto-generated method stub
		super.setError(bean);
	}

	@Override
	protected void setError(Bean bean, String msg) {
		// TODO Auto-generated method stub
		super.setError(bean, msg);
	}

	@Override
	protected boolean isOk(Bean bean) {
		// TODO Auto-generated method stub
		return super.isOk(bean);
	}

	@Override
	protected boolean isOkOrWarn(Bean bean) {
		// TODO Auto-generated method stub
		return super.isOkOrWarn(bean);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public boolean equals(Object arg0) {
		// TODO Auto-generated method stub
		return super.equals(arg0);
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	/**
     * 提供基于设定条件的查询，不处理分页、字典名称替换等,主要供非分页的服务间调用。
     * 
     * finds常用在对内部程序服务，缺省不进行分页，query对外部用户服务缺省采用服务的分页设定；
     * finds可以手工指定返回的最大记录数：param.setShowNum(40);
     * finds缺省select所有服务字段，可以参数重载：param.setSelect("TEST_ID,TEST_NAME");
     * finds的where缺省不做任何过滤，可以通过指定字段值或者指定where设定：param.setWhere("and 1=1");
     * 如果需使用服务定义的where，可以servDef.getServDefWhere()获取服务定义的总体where再通过where参数传递，支持动态查询规则
     * finds的order缺省使用服务定义的order设定，可以参数重载：param.setOrder("TEST_NAME desc");
     * finds支持分组重载设定：param.setGroup("TEST_TYPE"); 要求同时重载SELECT以配合分组语句;
     * 
     * @param param 参数信息
     * @return 输出信息
     */
    public OutBean finds(ParamBean param) {
        OutBean outBean = new OutBean();
        ParamBean paramBean = new ParamBean(param);
        String servId = paramBean.getServId();
        ServDefBean servDef = ServUtils.getServDef(servId);
        if (!paramBean.contains(Constant.PARAM_ORDER)) { //自动设置order排序，如果没有手工指定则从服务定义获取
            paramBean.setOrder(servDef.getSqlOrder());
        }
        List<Bean> dataList = ServDao.finds(servId, paramBean);
        outBean.setData(dataList).setCount(dataList.size());
        return outBean;
    }
    
    /**
     * 提供基于主键的查询服务
     * @param paramBean    参数Bean
     * @return 查询结果
     */
    public OutBean byid(ParamBean paramBean) {
        String servId = paramBean.getServId();
        beforeByid(paramBean);
        ServDefBean servDef = ServUtils.getServDef(servId);
        OutBean outBean = null;
        if (paramBean.getId().length() > 0) {
            Bean dataBean = ServDao.find(servId, paramBean.getId(), paramBean.getLinkFlag());
            if (dataBean == null) {
                if (paramBean.getByidAutoAddFlag()) { //设定了要自动添加一条记录
                    ParamBean param = paramBean.copyOf(); //允许在参数中设定各个字段属性
                    param.set(servDef.getPKey(), paramBean.getId()); //设置查询指定的主键为新添加的主键
                    param.setAct(ServMgr.ACT_SAVE).setAddFlag(true); //设置保存操作
                    outBean = ServMgr.act(param);
                } else {
                    throw new TipException(Context.getSyMsg("SY_BYID_ERROR", paramBean.getId()));
                }
            } else {
                outBean = new OutBean(dataBean);
            }
            checkActExpression(servDef, ServMgr.ACT_BYID, outBean); //执行操作表达式检查（使用byid操作）
        } else {
            outBean = new OutBean();
            outBean.set(servDef.getPKey(), Lang.getUUID());
            Map<String, Bean> items = servDef.getAllItems(); //支持自定义
            for (String key : items.keySet()) { //预处理初始值
                Bean item = items.get(key);
                if (item.isNotEmpty("ITEM_INPUT_DEFAULT")) { //预处理初始值
                    outBean.set(key, ServUtils.replaceSysVars(item.getStr("ITEM_INPUT_DEFAULT")));
                }
            }
            outBean.setByidAddFlag(true); //设置添加操作参数给前后端进行规则验证
            checkActExpression(servDef, ServMgr.ACT_ADD, outBean); //执行操作表达式检查（使用添加操作）
        }

        Map<String, Bean> items = servDef.getAllItems(); //支持自定义          
        String cmpy = null;
        String cmpyItem = servDef.getCmpy();
        if (cmpyItem.length() > 0) { //指定当前数据的公司编码
            cmpy = Context.changeCmpy(outBean.getStr(cmpyItem));
        }
        for (String key : items.keySet()) {
            Bean item = items.get(key);
            if (item.getInt("ITEM_USER_FLAG") == Constant.YES_INT) { //处理用户的各种信息
                appendUserItemInfo(key, outBean);
            } else  if (item.isNotEmpty("DICT_ID") && outBean.isNotEmpty(key)) { //字典处理名称
            		String dictId = item.getStr("DICT_ID");
	            	if (dictId.startsWith("@")) {
	        			ParamBean param = null;
	        			String config = item.getStr("ITEM_INPUT_CONFIG");
	        			Bean configBean = JsonUtils.toBean(config.substring(config.indexOf(Constant.SEPARATOR) + 1));
	        			param = new ParamBean(configBean.getBean("params"));
	        			dictId = DictMgr.getDictId(dictId.substring(1), param);
	        		}
                outBean.set(key + "__NAME", 
                    DictMgr.getFullNames(dictId, outBean.getStr(key)));
            }
        }
        if (servDef.hasMindLable()) { //启用了意见lable模式（包含意见lable字段）
            //填充意见内容到单条数据中。
            MindLabelContentProvider mlcProvider = new MindLabelContentProvider(servId
                    , outBean.getId());
            mlcProvider.fillData(Context.getUserBean(), outBean);                
        }
        if (outBean.isNotEmpty("S_WF_INST")) { //此数据在工作流中
            ProcDefServ procServ = new ProcDefServ();
            procServ.addWfInfoToOut(servId, outBean, paramBean); //进行工作流相关逻辑处理
        }
        if (cmpy != null) { //恢复当前用户的公司编码
            Context.changeCmpy(cmpy);
        }

        afterByid(paramBean, outBean);
        return outBean;
    }
    

    /**
     * 提供移动手机基于主键的查询服务
     * @param paramBean    参数Bean
     * @return 查询结果
     */
    public OutBean byidMB(ParamBean paramBean) {
        String servId = paramBean.getServId();
        beforeByid(paramBean);
        ServDefBean servDef = ServUtils.getServDef(servId);
        //总输出：serv/form/wf/mind
        OutBean out = new OutBean();
        OutBean outBean = null;
        
        if (paramBean.getId().length() > 0) {
            Bean dataBean = ServDao.find(servId, paramBean.getId(), paramBean.getLinkFlag());
            if (dataBean == null) {
                if (paramBean.getByidAutoAddFlag()) { //设定了要自动添加一条记录
                    ParamBean param = paramBean.copyOf(); //允许在参数中设定各个字段属性
                    param.set(servDef.getPKey(), paramBean.getId()); //设置查询指定的主键为新添加的主键
                    param.setAct(ServMgr.ACT_SAVE).setAddFlag(true); //设置保存操作
                    outBean = ServMgr.act(param);
                } else {
                    throw new TipException(Context.getSyMsg("SY_BYID_ERROR", paramBean.getId()));
                }
            } else {
                outBean = new OutBean(dataBean);
            }
            checkActExpression(servDef, ServMgr.ACT_BYID, outBean); //执行操作表达式检查（使用byid操作）
        } else {
            outBean = new OutBean();
            outBean.set(servDef.getPKey(), Lang.getUUID());
            Map<String, Bean> items = servDef.getAllItems(); //支持自定义
            for (String key : items.keySet()) { //预处理初始值
                Bean item = items.get(key);
                if (item.isNotEmpty("ITEM_INPUT_DEFAULT")) { //预处理初始值
                    outBean.set(key, ServUtils.replaceSysVars(item.getStr("ITEM_INPUT_DEFAULT")));
                }
            }
            outBean.setByidAddFlag(true); //设置添加操作参数给前后端进行规则验证
            checkActExpression(servDef, ServMgr.ACT_ADD, outBean); //执行操作表达式检查（使用添加操作）
        }

        Map<String, Bean> items = servDef.getAllItems(); //支持自定义          
        String cmpy = null;
        String cmpyItem = servDef.getCmpy();
        if (cmpyItem.length() > 0) { //指定当前数据的公司编码
            cmpy = Context.changeCmpy(outBean.getStr(cmpyItem));
        }
        for (String key : items.keySet()) {
            Bean item = items.get(key);
            if (item.getInt("ITEM_USER_FLAG") == Constant.YES_INT) { //处理用户的各种信息
                appendUserItemInfo(key, outBean);
            } else  if (item.isNotEmpty("DICT_ID") && outBean.isNotEmpty(key)) { //字典处理名称
            		String dictId = item.getStr("DICT_ID");
	            	if (dictId.startsWith("@")) {
	        			ParamBean param = null;
	        			String config = item.getStr("ITEM_INPUT_CONFIG");
	        			Bean configBean = JsonUtils.toBean(config.substring(config.indexOf(Constant.SEPARATOR) + 1));
	        			param = new ParamBean(configBean.getBean("params"));
	        			dictId = DictMgr.getDictId(dictId.substring(1), param);
	        		}
                outBean.set(key + "__NAME", 
                    DictMgr.getFullNames(dictId, outBean.getStr(key)));
            }
        }
        if (servDef.hasMindLable()) { //启用了意见lable模式（包含意见lable字段）
            //填充意见内容到单条数据中。
            MindLabelContentProvider mlcProvider = new MindLabelContentProvider(servId
                    , outBean.getId());
            mlcProvider.fillData(Context.getUserBean(), outBean);                
        }
        if (outBean.isNotEmpty("S_WF_INST")) { //此数据在工作流中
            ProcDefServ procServ = new ProcDefServ();
            procServ.addWfInfoToOut(servId, outBean, paramBean); //进行工作流相关逻辑处理
            
//            ParamBean mindTemp = new ParamBean();
//            mindTemp.setWhere(" and DATA_ID='"+paramBean.getId()+"'");
//            int count = ServDao.count("SY_COMM_MIND", mindTemp);
			Bean mindBean = new Bean();
			/* 显示的意见列表 的 单位名称 */
			ParamBean param = new ParamBean();
			param.set("GENERAL", JsonUtils.toJson(outBean
					.getStr("mindCodeBean").length() > 0 ? outBean
					.getBean("mindCodeBean") : new Bean()));
			param.set(
					"REGULAR",JsonUtils.toJson(outBean.getStr("regularMind").length() > 0 ? outBean
									.getBean("regularMind") : new Bean()));
			param.set("TERMINAL", JsonUtils.toJson(outBean.getStr(
					"mindTerminal").length() > 0 ? outBean
					.getBean("mindTerminal") : new Bean()));
			param.setServId("SY_COMM_MIND");
			param.setAct("showMindInputMB");
			mindBean.set("mindInput", ServMgr.act(param));

			param.clear();
			param.set("DEL_MIND", true);
			param.set("_NOPAGE_", "YES");
			param.set("DATA_ID", paramBean.getId());
			param.set("SERV_ID", servDef.getSrcId());
			param.set("_extWhere", " AND S_FLAG=1");
			param.set("CAN_COPY", true);
			param.set("SORT_TYPE", "TIME");
			if (outBean.getBean("authBean") != null) {
				param.set("userDoInWf",
						outBean.getBean("authBean").get("userDoInWf", false));
			}
			if (outBean.getBean("nodeInstBean") != null) {
				param.set("NI_ID",
						outBean.getBean("nodeInstBean").getStr("NI_ID"));
			}
			param.setServId("SY_COMM_MIND");
			param.setAct("displayMindTitleMB");
			mindBean.set("mindDatas", ServMgr.act(param));

			out.set("mind", mindBean);
        }
        //file Bean
        ParamBean fileParam = new ParamBean();
        fileParam.set("DATA_ID", paramBean.getId());
        fileParam.setServId("SY_COMM_FILE");
        OutBean fileOutBean = this.finds(fileParam);
        if(fileOutBean.getCount()>0){
        	out.set("file", fileOutBean);
        }
        if (cmpy != null) { //恢复当前用户的公司编码
            Context.changeCmpy(cmpy);
        }
        
        //LOAD_SERV_DATA标识是否需加载服务信息
        if (paramBean.getBoolean("LOAD_SERV_DATA")) {
        	out.set("serv", this.serv(paramBean));
        }
        
        afterByid(paramBean, outBean);
        afterByidCardTmpl(paramBean,out.getBean("serv"));
        out.set("form", outBean);
        System.out.println(out.get("_data"));
        return out;
    }
    
    /**
     * 根据paramBean参数组装的where条件获取数据的数量
     * @param paramBean 参数信息
     * @return 符合条件的数据数量
     */
    public  OutBean count(ParamBean paramBean) {
        String servId = paramBean.getServId();
        OutBean outBean = new OutBean();
        paramBean.set(SERV_PARAM.$SERV_CACHES, Constant.CACHE_FLAG_NO);
        int count = ServDao.count(servId, paramBean);
        outBean.setData(count).setCount(count);
        return outBean;
    }
    
    /**
     * 提供保存服务，有主键保存修改内容，没主键添加新数据。
     * @param paramBean    参数Bean，自动根据addFlag是否为true判断是添加保存还是修改保存
     * @return 保存结果
     */
    public OutBean save(ParamBean paramBean) {
        OutBean outBean = null;
        String servId = paramBean.getServId();
        ServDefBean servDef = ServUtils.getServDef(servId);
        //传递级联处理参数并执行保存方法
        if (paramBean.getId().length() == 0) { //如果缺省没有KEY，则为添加模式
            paramBean.setAddFlag(true);
        }
        if (paramBean.getAddFlag()) { //保存强制添加标志或者没有主键，则自动为添加模式
            outBean = add(paramBean);
        } else {
            outBean = modify(paramBean);
        }
        if (outBean.isOkOrWarn()) {
            outBean.setSaveIds(outBean.getId()); //设置成功的ID给消息机制使用
            servDef.clearDictCache(); //清除对应的字典缓存
            ServUtils.saveIndex(servDef, outBean); //处理全文索引信息
        }
        return outBean;
    }
    /**
     * save方法之前的操作，在判断添加或者保存之前进行处理
     * @param paramBean
     */
    

	/**
     * 提供添加新数据的保存服务。
     * @param paramBean 参数Bean
     * @return 保存结果
     */
    protected OutBean add(ParamBean paramBean) {
        //初始化参数，获取数据库原始数据，检查各种规则验证许可========================================
        String servId = paramBean.getServId();
        ServDefBean servDef = ServUtils.getServDef(servId);
        String oldId = paramBean.getId();
        String newId = paramBean.getStr(servDef.getPKey());
        if (newId.length() == 0) { //如果新ID不存在，提前预制ID，确保树形字典等保存前置相关信息处理正确
            newId = Lang.getUUID();
            paramBean.set(servDef.getPKey(), newId);
        }
        //不再根据_PK_判断是否添加，改为'#_ADD_#'=='true'来判断是否为添加
        checkActExpression(servDef, ServMgr.ACT_SAVE, paramBean); //执行操作表达式检查
        //前监听========================================
        beforeSave(paramBean);
        if (servDef.getDictCodes().length() > 0) { //如果树形服务，需要处理树形路径
            beforeAddTreeDictField(servDef, paramBean);
        }
        Bean dataBean = ServDao.create(servId, paramBean);
        OutBean outBean;
        if (dataBean != null) {
            outBean = new OutBean(dataBean);
            if (!oldId.equals(newId)) { //处理主键字段与预制主键不一致的情况
                updateRelatePKey(servDef, oldId, newId);
            }
            //处理子功能批量添加，必须运行在级联模式下
            if (servDef.hasLink()) { //执行级联添加
                //进行关联删除的判断
                LinkedHashMap<String, Bean> links = servDef.getAllLinks();
                for (String key : links.keySet()) {
                    Bean link = links.get(key);
                    if (link.getInt("LNKE_READONLY") == Constant.YES_INT) { //忽略只读关联
                        continue;
                    }
                    String linkServ = link.getStr("LINK_SERV_ID");
                    if (paramBean.contains(linkServ)) { //存在级联添加的数据
                        ParamBean linkParam = new ParamBean(linkServ, ServMgr.ACT_BATCHSAVE);
                        linkParam.setBatchSaveDatas(paramBean.getList(linkServ));
                        linkParam.set(Constant.IS_LINK_ACT, true); //设定为级联模式
                        linkParam.setAddFlag(true); //传递添加模式标志
                        linkParam.set("SERV_ID", link.getStr("LINK_SERV_ID"));
                        ServMgr.act(linkParam);
                    }
                }
            }
            outBean.setOk(Context.getSyMsg("SY_ADD_OK"));
            //执行保存后监听
            afterSave(paramBean, outBean); //确保添加的后监听在流程启动前完成
            //处理工作流的流程绑定
            if (servDef.hasWfAuto() && startWf(servId, outBean)) { //启动流程，如果成功重载提示信息（流程会自动设置实体信息）
                outBean.setOk(Context.getSyMsg("SY_WF_START_OK"));
            } else { //如果没有启动流程，则直接设定标题项，存入实体表
                if (servDef.getDataTitle().length() > 0) {
                    EntityMgr.addEntity(servDef, outBean, paramBean);
                }
            }
        } else {
            outBean = new OutBean();
            outBean.setError(Context.getSyMsg("SY_ADD_ERROR"));
            //即使错误也执行保存后监听
            afterSave(paramBean, outBean);
        }
        return outBean;
    }
    
    /**
     * 提供修改数据的保存服务。
     * @param paramBean 参数Bean
     * @return 保存结果
     */
    private OutBean modify(ParamBean paramBean) {
        //初始化参数，获取数据库原始数据，检查各种规则验证许可========================================
        String servId = paramBean.getServId();
        ServDefBean servDef = ServUtils.getServDef(servId);
        ServDao oldBean = new ServDao(servId, paramBean.getId());
        if (!oldBean.load()) { //从数据库装载老数据
            throw new TipException(Context.getSyMsg("SY_SAVE_NOTEXISTS", paramBean.getId()));
        }        
        checkActExpression(servDef, ServMgr.ACT_SAVE, oldBean); //执行操作表达式检查
        //如果服务定义设定了判断乐观锁，进行更新时间的判断
        if (servDef.hasLock() && paramBean.contains("S_MTIME")) {
            if (!paramBean.getStr("S_MTIME").equals(oldBean.getStr("S_MTIME"))) { //更新时间不一致
                throw new TipException(Context.getSyMsg("SY_SAVE_MTIME_ERROR"));
            }
        }
        paramBean.set(ParamBean.SAVE_OLD_DATA, oldBean);
        //前监听========================================
        beforeSave(paramBean);
        if (servDef.getDictCodes().length() > 0) { //如果树形服务，需要处理树形路径
            beforeModifyTreeDictField(servDef, paramBean);
        }
        //数据预处理========================================
        OutBean outBean = new OutBean(oldBean); //要返回的最新数据信息
        boolean okFlag = false; //是否执行成功
        boolean changeFlag  = false; //判断数据项是否变更
        boolean uniqueFlag = false; //是否判断唯一性约束
        ArrayList<Bean> changeList = new ArrayList<Bean>();
        for (Object key : paramBean.keySet()) {
            Bean item = servDef.getItem(key);
            if (item != null) {
                if (!paramBean.getStr(key).equals(oldBean.getStr(key))) {
                    if (item.getInt("ITEM_LOG_FLAG") == Constant.YES_INT) { //处理数据变更留痕
                        Bean change = new Bean();
                        change.set("ITEM_ID", item.getId()).set("DATA_ID", oldBean.getId())
                        .set("ILOG_OLD", oldBean.get(key)).set("ILOG_NEW", paramBean.get(key))
                        .set("SERV_ID", servId).set("ITEM_CODE", item.getStr("ITEM_CODE"))
                        .set("ITEM_NAME", item.getStr("ITEM_NAME"));
                        
                        if (item.isNotEmpty("DICT_ID")) { //字典处理名称
                            String oldValue = DictMgr.getFullNames(item.getStr("DICT_ID"), oldBean.getStr(key));
                            String newValue = DictMgr.getFullNames(item.getStr("DICT_ID"), paramBean.getStr(key));
                            
                            change.set("ILOG_OLD", oldValue).set("ILOG_NEW", newValue);
                        }
                        
                        changeList.add(change);
                    }
                    outBean.set(key, paramBean.get(key)); //记录更新的内容到outBean中
                    if (!changeFlag) {
                        changeFlag = true;
                    }
                }
                if (item.getInt("ITEM_UNIQUE_GROUP") == Constant.YES_INT) { //变更的字段涉及唯一约束
                    uniqueFlag = true;
                }
            }
        }
        //数据正式处理========================================
        if (changeFlag) { //只有修改了才进行保存处理
            //进行组合值的处理
            List<Bean> combineItems = servDef.getCombineItems();
            for (Bean item : combineItems) { //处理组合值字段
                String itemCode = item.getStr("ITEM_CODE");
                String primaryKey = servDef.getPKey();
                if (!itemCode.equalsIgnoreCase(primaryKey)) {
                    String combine = ServUtils.genCombineItem(servId, itemCode, 
                            item.getStr("ITEM_INPUT_CONFIG"), outBean);
                    if (!combine.equals(outBean.getStr(itemCode))) { //组合值发生了变更
                    	if(outBean.getStr(itemCode).length()==0){//
                    		paramBean.set(itemCode, combine);
                    		outBean.set(itemCode, combine);
                    	}
                    }
                }
            }
            //进行唯一组约束的判断
            if (uniqueFlag) {
                String uniqueStr = ServUtils.checkUniqueExists(servDef, outBean, false);
                if (uniqueStr != null) {
                    throw new TipException(Context.getSyMsg("SY_SAVE_UNIQUE_EXISTS", uniqueStr));
                }
            }
            okFlag = (ServDao.update(servId, paramBean) != null);
            if (okFlag) {
                outBean.set("S_MTIME", paramBean.get("S_MTIME")); //传递更新时间
                String oldId = paramBean.getId();
                String newId = paramBean.getStr(servDef.getPKey());
                if (newId.length() == 0) { //新ID如果没有值，说明没有变更主键
                    newId = oldId;
                }
                //如果设定标题项，且存在变更，存入实体表
                if (servDef.getDataTitle().length() > 0
                        && (paramBean.contains("S_WF_NODE") || paramBean.contains("S_EMERGENCY") 
                                || BeanUtils.containsValue(servDef.getDataTitle(), paramBean)
                                || BeanUtils.containsValue(servDef.getDataCode(), paramBean)
                                || !newId.equals(oldId))) {
                    EntityMgr.updateEntity(servDef, outBean, paramBean);
                }
                if (!newId.equals(oldId)) { //主键发生变化，更新相关的附件等
                    outBean.setId(newId);
                    updateRelatePKey(servDef, oldId, newId); 
                }
            }
        }
        //关联服务数据处理========================================
        String linkMsg = "";
        if (servDef.hasLink()) { //执行级联修改
            OutBean linkOutBean = linkUpdate(servId, oldBean, paramBean);
            if (linkOutBean.isOk()) {
                if (!okFlag) {
                    okFlag = true;
                }
            } else {
                changeFlag = true;
                linkMsg = linkOutBean.getStr(Constant.RTN_MSG);
            }
        }
        //执行成功后清除缓存记录历史等
        if (okFlag) { //保存执行成功
            outBean.setOk(Context.getSyMsg("SY_SAVE_OK")); //先处理成功标志，后面统一处理文件
            //进行变更历史的记录
            if (!changeList.isEmpty()) {
                ServUtils.itemLog(changeList);
            }
        } else {
            if (changeFlag) {
                if (linkMsg.length() > 0) {
                    outBean.setMsg(linkMsg);
                } else {
                    outBean.setError(Context.getSyMsg("SY_SAVE_ERROR"));
                }
            } else {
                outBean.setWarn(Context.getSyMsg("SY_SAVE_NOCHANGE"));
            }
        }
        //保存后方法监听========================================
        afterSave(paramBean, outBean);
        return outBean;
    }

    /**
     * 提供批量保存服务服务
     * @param paramBean 输入参数信息，要求必须有BATCHDATAS存放要保存的数据数组。
     * @return 执行结果
     */
    public OutBean batchSave(ParamBean paramBean) {
        String servId = paramBean.getServId();
        OutBean outBean = new OutBean();
        String errorMsg = "";  //错误信息
        String delMsg = "";  //删除信息
        int saveCount = 0;  //保存成功数量
        int saveError = 0;   //保存失败数量
        StringBuilder okIds = new StringBuilder(); //操作成功的ID列表
        beforeBatchSave(paramBean);  //批量保存前监听方法
        String batchDels = paramBean.getBatchSaveDelIds();
        List<Bean> batchSaves = paramBean.getBatchSaveDatas();
        for (Bean data : batchSaves) {
            ParamBean param = new ParamBean(data);
            try {
                //初始化参数，传递服务编码、添加模式标志、级联处理子数据标志、处于级联模式下标志
                param.setServId(servId).setAddFlag(paramBean.getAddFlag()).setLinkFlag(paramBean.getLinkFlag());
                param.set(Constant.IS_LINK_ACT, paramBean.getLinkMode()); //传递级联模式
                OutBean out = save(param);
                if (out.isOk()) {
                    okIds.append(out.getId()).append(",");
                    saveCount++;
                }
            } catch (Exception e) { //记录失败的数据
                errorMsg = errorMsg + " " + e.getMessage();
                log.error(e.getMessage(), e);
                saveError++;
            }
        }
        outBean.setSaveIds(okIds.toString());
        if (batchDels.length() > 0) { //执行批量删除
            ParamBean delParam = new ParamBean();
            delParam.setServId(servId).setLinkFlag(paramBean.getLinkFlag())
                .set(Constant.IS_LINK_ACT, paramBean.getLinkMode()).setId(batchDels);
            OutBean out = delete(delParam);
            delMsg = out.getMsg();
            outBean.setDeleteIds(out.getDeleteIds());
        }
        String msg = "";
        if (saveCount > 0) {
            msg = Context.getSyMsg("SY_BATCHSAVE_OK", String.valueOf(saveCount));
        }
        if (saveError > 0) {
            msg = msg + " " + Context.getSyMsg("SY_BATCHSAVE_ERROR", String.valueOf(saveError)) + errorMsg;
        }
        if (delMsg.length() > 0) {
            msg = msg + "  " + delMsg;
        }
        outBean.setDeleteIds(okIds.toString());
        if (saveCount > 0) {
            if (saveError > 0) {
                outBean.setWarn(msg);
            } else {
                outBean.setOk(msg);
            }
        } else {
            if (msg.length() > 0) {
                outBean.setError(msg);
            } else {
                outBean.setError(Context.getSyMsg("SY_BATCHSAVE_NONE"));
            }
        }
        afterBatchSave(paramBean, outBean);  //批量保存后监听方法
        return outBean;
    }
    
    /**
     * 提供基于主键的删除服务，从权限考虑提供基于where条件的删除
     * 参数支持两种：（只能二选一，不支持一起设定，设定了ids就不再判断dataList）
     *  1.通过paramBean.setId(String ids); 设置需要删除的主键列表，多条主键逗号分隔，删除时需要二次检索取出数据进行逻辑验证；
     *  2.通过paramBean.setDeleteDatas(List<Bean> dataList);将待删除的数据列表传入，删除时无需二次检索，性能更好；
     * @param paramBean    参数Bean
     * @return 删除结果
     */
    public OutBean delete(ParamBean paramBean) {
        //预处理待删除数据及删除模式（真删除还是假删除）========================================
        String servId = paramBean.getServId();
        ServDefBean servDef = ServUtils.getServDef(servId);
        boolean falseDel = servDef.hasFalseDelete();
        if (paramBean.getDeleteDropFlag()) { //如果指定了强制删除，则假删除为false
            falseDel = false;
        } else {
            paramBean.setDeleteDropFlag(!falseDel); //设置删除标志给监听方法
        }
        List<Bean> dataList;
        if (paramBean.getId().length() > 0) { //优先获取待删除主键列表
            dataList = ServDao.finds(servId, " and " + servDef.getPKey() + " in ('" 
                    + paramBean.getId().replaceAll(",", "','") + "')");
            paramBean.setDeleteDatas(dataList); //给删除前后扩展方法使用
        } else { //其次判断待删除数据列表
            dataList = paramBean.getDeleteDatas();
        }
        //删除前方法监听========================================
        beforeDelete(paramBean);
        int count = 0; //删除成功数量
        int error = 0; //删除失败数量
        StringBuilder sbError = new StringBuilder(); //删除失败信息
        StringBuilder okIds = new StringBuilder(); //操作成功的ID列表
        List<Bean> okList = new ArrayList<Bean>(); //操作成功的数据列表
        for (Bean dataBean : dataList) {
            String id = dataBean.getId();
            try {
                //单条删除前置处理========================================
                checkActExpression(servDef, ServMgr.ACT_DELETE, dataBean); //执行操作表达式检查
                if (servDef.getDictCodes().length() > 0) { //如果树形服务，需要处理是否包含子数据的情况
                    beforeDeleteTreeDict(servDef, dataBean);
                }
                boolean okFlag = false;
                deleteWf(dataBean, falseDel); //工作流删除
                if (paramBean.getLinkFlag()) { //强制级联删除
                    linkDelete(servId, dataBean, !falseDel, true); 
                } else if (servDef.hasLink()) { //非强制级联删除
                    linkDelete(servId, dataBean, !falseDel, false); 
                }
                //单条删除处理========================================
                if (falseDel) { //假删除
                    okFlag = ServDao.delete(servId, dataBean);
                } else { //真删除
                    okFlag = ServDao.destroy(servId, dataBean);
                    if (okFlag) { //真删除成功后，处理关联数据项删除：文件、索引、相关文件
                        if (servDef.hasFile()) { //删除对应文件上传
                            ServUtils.deleteFile(servDef, id);
                        }
                        if (servDef.hasRelate()) { //设定了相关文件，则删除相关文件
                            RelateMgr.deleteRelate(servDef, id);
                        }
                        if (servDef.hasSearch()) { // 设置启用了全文搜索
                            ServUtils.deleteIndex(servDef, id); //清除索引
                        }
                    }
                }
                //单条删除后置处理========================================
                if (okFlag) { //删除成功的通用处理
                    okList.add(dataBean);
                    okIds.append(id).append(",");
                    count++;
                    if (servDef.hasEntity()) { //如果对应实体数据，删除对应的实体表
                        EntityMgr.deleteEntity(servDef, dataBean, falseDel);
                    }
                }
            } catch (TipException e) {
                error++;
                sbError.append(" ").append(id).append(":").append(e.getMessage());
            } catch (Exception e) {
                error++;
                sbError.append(" ").append(id).append(":").append(e.getMessage());
                log.error(e.getMessage(), e);
            }
        }
        OutBean outBean = new OutBean();
        String msg = "";
        if (count > 0) {
            okIds.setLength(okIds.length() - 1);
            msg = Context.getSyMsg("SY_DELETE_OK", String.valueOf(count));
            outBean.setOk(msg);
        }
        if (error > 0) {
            if (sbError.length() > 0) {
                msg = msg + " " + sbError;
            } else {
                msg = msg + " " + Context.getSyMsg("SY_DELETE_ERROR", String.valueOf(error));
            }
        }
        outBean.setCount(count);
        outBean.setDeleteIds(okIds.toString());
        outBean.setData(okList); //将成功删除的数据设置到参数中供删除之后的方法使用
        //删除后方法监听========================================
        afterDelete(paramBean, outBean);
        if (count > 0) {
            servDef.clearDictCache(); //清除对应的字典缓存
            if (error > 0) {
                outBean.setWarn(msg);
            } else {
                outBean.setOk(msg);
            }
        } else {
            if (msg.length() > 0) {
                outBean.setError(msg);
            } else {
                outBean.setWarn(Context.getSyMsg("SY_DELETE_NONE"));
            }
        }
        return outBean;
    }
    
    /**
     * 提供导入服务
     * @param paramBean 参数信息
     * @return 执行结果
     */
    public OutBean imp(ParamBean paramBean) {
    	OutBean outBean = new OutBean();
        beforeImp(paramBean); //执行监听方法
        String servId = paramBean.getServId();
        ServDefBean servDef = ServUtils.getServDef(servId);
        LinkedHashMap<String, Bean> titleMap = BeanUtils.toLinkedMap(servDef.getTableItems(), "ITEM_NAME");
        String fileId = paramBean.getStr("fileId");
        Bean fileBean = FileMgr.getFile(fileId);
        if (fileBean != null && fileBean.getStr("FILE_MTYPE").equals("application/vnd.ms-excel")) { //只支持excel类型
            Workbook book = null;
            InputStream in = null;
            try {
                in = FileMgr.download(fileBean);
                //打开文件 
                try {
                    book = Workbook.getWorkbook(in) ;
                } catch(Exception e) {
                    log.error(e.getMessage(), e);
                    throw new RuntimeException("Wrong file format, only suport 2003 and lower version," 
                            + "pls use export excel file as the template!");
                }
                //取得第一个sheet  
                Sheet sheet = book.getSheet(0);  
                //取得行数  
                int rows = sheet.getRows();
                List<Bean> dataList = new ArrayList<Bean>(rows);
                Cell[] titleCell = sheet.getRow(0);
                int cols = titleCell.length;
                Bean[] itemMaps = new Bean[cols];
                for (int j = 0; j < cols; j++) { //第一行标题列，进行标题与字段的自动匹配，优先匹配中文名称，其次配置编码
                    String title = sheet.getCell(j, 0).getContents();
                    Bean itemMap = null;
                    if (titleMap.containsKey(title)) {
                        itemMap = titleMap.get(title);
                    } else {
                        itemMap = servDef.getItem(title);
                    }
                    if (itemMap != null) {
                        itemMaps[j] = itemMap;
                    }
                }
                for(int i = 1; i < rows; i++) {
                    Cell [] cell = sheet.getRow(i);
                    Bean data = new Bean();
                    for(int j = 0; j < cell.length; j++) {
                        if (itemMaps[j] != null) {
                            String value = sheet.getCell(j, i).getContents();
                            if (itemMaps[j].isNotEmpty("DICT_ID")) { //字典处理名称和值的转换
                                String dictVal = DictMgr.getItemCodeByName(itemMaps[j].getStr("DICT_ID"), value);
                                if (dictVal != null) {
                                    value = dictVal;
                                }
                            }
                            data.set(itemMaps[j].getStr("ITEM_CODE"), value);
                        }
                    }
                    dataList.add(data);
                }  
                //关闭文件  
                book.close();
                book = null;
                if (dataList.size() > 0) {
                    ParamBean param = new ParamBean(servId, ServMgr.ACT_BATCHSAVE);
                    param.setBatchSaveDatas(dataList);
                    outBean = ServMgr.act(param);
                } else {
                    outBean.setWarn("");
                }
            } catch (Exception e) {  
                throw new RuntimeException(e.getMessage(), e);  
            } finally {
                if (book != null) {
                    book.close();
                }
                IOUtils.closeQuietly(in);
            }
        } else { //错误的文件内容或格式
            outBean.setWarn("");
        }
        FileMgr.deleteFile(fileBean); //最后删除临时上传的文件
        return outBean;
    }

    

    /**
     * 提供导出服务
     * @param paramBean 参数信息
     * @return 执行结果
     */
	public OutBean exp(ParamBean paramBean) {
		String servId = paramBean.getServId();
		ServDefBean serv = ServUtils.getServDef(servId);
		int count = 0;
		int times = 0;
		paramBean.setQueryPageShowNum(ONETIME_EXP_NUM); // 设置每页最大导出数据量
		beforeExp(paramBean); // 执行监听方法
		if (paramBean.getId().length() > 0) { // 支持指定记录的导出（支持多选）
			String searchWhere = " and " + serv.getPKey() + " in ('"
					+ paramBean.getId().replaceAll(",", "','") + "')";
			paramBean.setQuerySearchWhere(searchWhere);
		}
		ExportExcel expExcel = new ExportExcel(serv);
		try {
			OutBean outBean = query(paramBean);
			count = outBean.getCount();
			//总数大于excel可写最大值
			if (count > EXCEL_MAX_NUM) {
				return new OutBean().setError("导出数据总条数大于Excel最大行数：" + EXCEL_MAX_NUM);
			}
			//导出第一次查询数据
			paramBean.setQueryPageNowPage(1); //导出当前第几页
			afterExp(paramBean, outBean); //执行导出查询后扩展方法
			expExcel.createHeader(outBean.getCols());
			expExcel.appendData(outBean.getDataList(), paramBean);

			// 存在多页数据
			if (ONETIME_EXP_NUM < count) {
				times = count / ONETIME_EXP_NUM;
				// 如果获取的是整页数据
				if (ONETIME_EXP_NUM * times == count && count != 0) {
					times = times - 1;
				}
				for (int i = 1; i <= times; i++) {
					paramBean.setQueryPageNowPage(i + 1); // 导出当前第几页
					OutBean out = query(paramBean);
					afterExp(paramBean, out); // 执行导出查询后扩展方法
					expExcel.appendData(out.getDataList(), paramBean);
				}
			}
			expExcel.addSumRow();
		} catch (Exception e) {
			log.error("导出Excel文件异常" + e.getMessage(),e);
		}finally {
			expExcel.close();
		}
		return new OutBean().setOk();
	}
    
    /**
     * 导出压缩包格式数据，支持数据的级联导出，支持多个格式json、xml
     * @param paramBean 参数信息
     * @return 执行结果
     */
    public OutBean expZip(ParamBean paramBean) {
        ServDefBean servDef = ServUtils.getServDef(paramBean.getServId());
        beforeExpZip(paramBean); //执行监听方法
        String ids = paramBean.getId();   //要导出的数据ID
        StringBuffer where = new StringBuffer();
        if (ids.length() > 0) { //指定ID
            where.append(" and ").append(servDef.getPKey()).append(" in('")
                .append(ids.replaceAll(Constant.SEPARATOR, "'" + Constant.SEPARATOR + "'")).append("')");
        } else { //没有指定ID，则导出全部查询结果
            where.append(getFullWhere(paramBean));
        }
        paramBean.setWhere(where.toString());
        List<Bean> dataList = finds(paramBean).getDataList();
   
        HttpServletRequest request = Context.getRequest();
        HttpServletResponse response = Context.getResponse();
        response.setContentType("application/x-download");
        RequestUtils.setDownFileName(request, response, servDef.getId() + ".zip");  //指定导出格式及名字
        
        ZipOutputStream zipOut = null; //输出流
        InputStream is = null;  //输入流
        try {
            zipOut = new ZipOutputStream(response.getOutputStream());
            is = IOUtils.toInputStream(JsonUtils.toJson(dataList, false), Constant.ENCODING); //将菜单定义转为输入流
            zipOut.putNextEntry(new ZipEntry(servDef.getId() + ".json"));  //指定输出文件名称
            IOUtils.copyLarge(is, zipOut);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (is != null) {
                IOUtils.closeQuietly(is);  //关闭输入流
            }
            if (zipOut != null) {
                IOUtils.closeQuietly(zipOut); //关闭输出流
            }
            try {
                response.flushBuffer();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return new OutBean();
    }
    
    /**
     * 导入压缩数据，支持多条数据批量导入
     * @param paramBean 参数信息
     * @return 执行结果
     */
    public OutBean impZip(ParamBean paramBean) {
        beforeImpZip(paramBean); //执行监听方法
        String fileId = paramBean.getStr("fileId");
        Bean fileBean = FileMgr.getFile(fileId);
        int count = 0;
        int errorCount = 0;
        if (fileBean != null && fileBean.getStr("FILE_MTYPE").equals("application/zip")) {
            ZipInputStream zipIn = null;
            InputStream in = null;
            try {
                zipIn = new ZipInputStream(FileMgr.download(fileBean));
                in = new BufferedInputStream(zipIn);
                if (zipIn.getNextEntry() != null) {
                    List<Bean> dataList = FileHelper.listFromJsonFile(in);
                    for (Bean data: dataList) {
                        ParamBean param = new ParamBean(data);
                        param.setServId(paramBean.getServId()).setAct(ServMgr.ACT_SAVE)
                            .setAddFlag(true).setLinkFlag(true);  //级联添加模式
                        try {
                            if (ServMgr.act(param).isOkOrWarn()) {
                                count++;
                            } else {
                                errorCount++;
                            }
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                            errorCount++;
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            } finally {
                if (zipIn != null) {
                    IOUtils.closeQuietly(zipIn);
                }
            }
        }
        FileMgr.deleteFile(fileBean);
        OutBean outBean = new OutBean();
        String msg = Context.getSyMsg("SY_IMPORT_MSG", String.valueOf(count), String.valueOf(errorCount));
        if (count > 0) {
            outBean.setOk(msg);
        } else if (count == 0) { 
            outBean.setError(msg);
        } else {
            outBean.setWarn(msg);
        }
        return outBean;
    }
        
    /**
     * 先取得ById的数据，然后在执行beforePrint方法，获取扩展的打印数据
     * @param paramBean 参数信息
     * @return 返回需打印的数据Bean
     */
    public OutBean getPrintData(ParamBean paramBean) {
        ParamBean queryBean = new ParamBean();
        queryBean.set(Constant.PARAM_SERV_ID,
                paramBean.getServId()).set(Constant.KEY_ID, paramBean.getId());
        OutBean outBean = ServMgr.act(paramBean.getServId(), "byid", queryBean);
        
        this.beforePrint(paramBean, outBean);
        
        return outBean;
    }
}