package com.rh.lw.ct.serv;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.TipException;
import com.rh.core.base.Context.APP;
import com.rh.core.comm.FileMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.ServUtils;
import com.rh.core.serv.bean.SqlBean;
import com.rh.core.util.Constant;
import com.rh.core.util.DateUtils;
import com.rh.core.util.JsonUtils;
import com.rh.core.util.file.FileHelper;
import com.rh.lw.ct.mgr.ContractMgr;
import com.rh.oa.gw.util.AuditUtils;

/**
 * 合同管理
 * @author chensheng
 * 
 */
public class ContractServ extends CommonServ {

    private static Log log = LogFactory.getLog(ContractServ.class);

    /** 主表-合同审批单ID **/
    public static final String LW_CT_CONTRACT = "LW_CT_CONTRACT";
    /** 子表-供应商信息管理ID **/
    public static final String BN_HT_SUPPLIER_INFO_LINK = "BN_HT_SUPPLIER_INFO_LINK";
    /** 供应商信息查询ID **/
    public static final String BN_HT_SUPPLIER_INFO = "BN_HT_SUPPLIER_INFO";

    /** 合同修改稿**/
    public static final String XIUGAIGAO = "XIUGAIGAO";

    @Override
    protected void afterByid(ParamBean paramBean, OutBean outBean) {
        super.afterByid(paramBean, outBean);
        // 如果是添加模式生成编号
        if (outBean.getByidAddFlag()) {
            // 取得机关代字
            String word = AuditUtils.getOrgWord(ContractMgr.LW_CT_CONTRACT);
            if (StringUtils.isNotBlank(word)) {
                outBean.set(ContractMgr.COL_CT_WORD, word);
            } else {
                log.debug("当前部门没有配置合同(" + ContractMgr.LW_CT_CONTRACT + ")的机关代字！");
                outBean.setError("当前部门没有配置合同的机关代字！");
            }
            // 年份
            String year = Integer.toString(DateUtils.getYear());
            if (StringUtils.isBlank(outBean.getStr(ContractMgr.COL_CT_YEAR))) {
                outBean.set(ContractMgr.COL_CT_YEAR, year);
            } else {
                year = outBean.getStr(ContractMgr.COL_CT_YEAR);
            }
            // 取得流水号
            if (word != null) {
                ParamBean queryBean = new ParamBean()
                .set("CT_YEAR", outBean.getStr("CT_YEAR"));
//                .set("CT_WORD", word);
                int serialInt = AuditUtils.getSerial(queryBean, ContractMgr.LW_CT_CONTRACT, "CT_SERIAL");
                String serial = Integer.toString(serialInt);
                outBean.set(ContractMgr.COL_CT_SERIAL, serial);

                // 设置合成编号
                outBean.set(ContractMgr.COL_CT_CODE, word + "〔" + year + "〕" + serial + "号");
            }
        }

        // 是否具有权限
        String userId = Context.getUserBean().getCode();
        if (ContractMgr.isAuthOwner(paramBean, userId)) {
            outBean.set("AUTH_OWNER", "true");
        }
    }
    /**
     * @author lidongdong
     * 获取机关代字
     */
    protected void afterByidCardTmpl(ParamBean paramBean, Bean out) {
    	out.set("SERV_CARD_TMPL_CONTENT_MB", getCardTmplContentMBQB(paramBean,out));
    }
    public String getCardTmplContentMBQB(ParamBean paramBean, Bean out) {
    	try {
	        if (!Context.isDebugMode() && out.contains("SERV_CARD_TMPL_CONTENT_MB")) {
	            return out.getStr("SERV_CARD_TMPL_CONTENT_MB");
	        }
	        int pos = paramBean.getServId().indexOf("_");
	        String path = Context.appStr(APP.SYSPATH) + paramBean.getServId().substring(0, pos).toLowerCase() + "/tmpl/"
	                + paramBean.getServId()+"_MB" + ".html";
	        return FileHelper.readFile(path);
    	} catch (Exception e) {
    		log.info(e.getMessage());
    		return "";
    	}
    }

    @Override
    protected void beforeSave(ParamBean paramBean) {
        super.beforeSave(paramBean);
        String word = paramBean.getStr(ContractMgr.COL_CT_WORD);
        String year = paramBean.getStr(ContractMgr.COL_CT_YEAR);
        String serial = paramBean.getStr(ContractMgr.COL_CT_SERIAL);
        if (StringUtils.isNotBlank(word) || StringUtils.isNotBlank(year) || StringUtils.isNotBlank(serial)) {
            // 校验合同编号有没有重复
            ParamBean queryBean = new ParamBean();
            Bean fullBean = paramBean.getSaveFullData();
            String fullWord = fullBean.getStr(ContractMgr.COL_CT_WORD);
            String fullYear = fullBean.getStr(ContractMgr.COL_CT_YEAR);
            String fullSerial = fullBean.getStr(ContractMgr.COL_CT_SERIAL);
            if (StringUtils.isNotBlank(fullWord) 
                    && StringUtils.isNotBlank(fullYear) && StringUtils.isNotBlank(fullSerial)) {
                queryBean.set(ContractMgr.COL_CT_WORD, fullWord);
                queryBean.set(ContractMgr.COL_CT_YEAR, fullYear);
                queryBean.set(ContractMgr.COL_CT_SERIAL, fullSerial);
                queryBean.set("S_FLAG", Constant.YES_INT);
                List<Bean> ctList = ServDao.finds(ContractMgr.LW_CT_CONTRACT, queryBean);
                if (ctList.size() > 0) {
                    throw new TipException("合同编号重复，重复的合同编号为" + fullWord + "〔" + fullYear + "〕" + fullSerial + "号");
                }
            }

            // 如果没有重复则生成合成一起的合同编号，便于查询
            paramBean.set(ContractMgr.COL_CT_CODE, fullWord + "〔" + fullYear + "〕" + fullSerial + "号");
        }
    }

    @Override
    protected void beforeExp(ParamBean paramBean) {
        super.beforeExp(paramBean);
        // 加上台账库的过滤条件
        String queryWhere = ServUtils.getServDef(ContractMgr.LW_CT_CONTRACT_BOOK).getServExpressionWhere();
        paramBean.setQueryExtWhere(queryWhere);
    }

    //保存合同表单后根据选择的供应商名称自动生成子服务的供应商信息
    @Override
    protected void afterSave(ParamBean paramBean, OutBean outBean) {
        //保存之前的数据
        Bean oldData = paramBean.getSaveOldData();
        //保存之后的数据
        Bean newData = paramBean.getSaveFullData();
        if (!oldData.getStr("CT_ST_NAME").equals(newData.getStr("CT_ST_NAME"))) {//修改了供应商信息，则先删除原来的供应商信息，再保存新的供应商
            // 获取签约方/供应商名称
            String  CT_ST_NAME = paramBean.getStr("CT_ST_NAME");
            // 获取合同ID
            String HT_ID = paramBean.getStr("_PK_");
            //根据合同ID查找本合同的供应商
            SqlBean delBean = new SqlBean();
            delBean.and("HT_ID", HT_ID);
            List<Bean> deleteBeans = ServDao.finds(BN_HT_SUPPLIER_INFO_LINK, delBean);
            //删除查找到的之前保存的供应商信息
            if (deleteBeans != null) {
                for (Bean deleteBean : deleteBeans) {
                    ServDao.delete(BN_HT_SUPPLIER_INFO_LINK, deleteBean);
                }
            }
            //根据供应商名称查找到信息并将信息复制到供应商关联表中
            if (!"".equals(CT_ST_NAME)) {
                String[] CT_ST_NAMES = CT_ST_NAME.split(",");
                if (CT_ST_NAMES != null && CT_ST_NAMES.length > 0) {
                    for (String name : CT_ST_NAMES) {
                        SqlBean sqlBean = new SqlBean();
                        sqlBean.and("SUPPLIER_NAME", name);
                        Bean assignBean = ServDao.find(BN_HT_SUPPLIER_INFO, sqlBean);
                        Bean linkData = new Bean();
                        linkData.set("HT_ID", HT_ID);
                        //                    linkData.set("SUPPLIER_ID", assignBean.get("SUPPLIER_ID"));
                        linkData.set("SUPPLIER_NAME", name);
                        linkData.set("SUPPLIER_TYPE", assignBean.get("SUPPLIER_TYPE"));
                        linkData.set("SUPPLIER_CAPITAL", assignBean.get("SUPPLIER_CAPITAL"));
                        linkData.set("SUPPLIER_ADDRESS", assignBean.get("SUPPLIER_ADDRESS"));
                        linkData.set("SUPPLIER_ZIPCODE", assignBean.get("SUPPLIER_ZIPCODE"));
                        linkData.set("SUPPLIER_PHONE", assignBean.get("SUPPLIER_PHONE"));
                        linkData.set("SUPPLIER_FAX", assignBean.get("SUPPLIER_FAX"));
                        linkData.set("SUPPLIER_WEBSITE", assignBean.get("SUPPLIER_WEBSITE"));
                        linkData.set("SUPPLIER_CORPORATIO", assignBean.get("SUPPLIER_CORPORATIO"));
                        linkData.set("SUPPLIER_CREATETIME", assignBean.get("SUPPLIER_CREATETIME"));
                        linkData.set("SUPPLIER_BANK", assignBean.get("SUPPLIER_BANK"));
                        linkData.set("SUPPLIER_FONDER", assignBean.get("SUPPLIER_FONDER"));
                        linkData.set("SUPPLIER_STATUS", assignBean.get("SUPPLIER_STATUS"));
                        linkData.set("SUPPLIER_ISCHECK", assignBean.get("SUPPLIER_ISCHECK"));
                        linkData.set("SUPPLIER_LICENSE_END", assignBean.get("SUPPLIER_LICENSE_END"));
                        linkData.set("SUPPLIER_LICENSE_START", assignBean.get("SUPPLIER_LICENSE_START"));
                        linkData.set("SUPPLIER_ACCOUNT", assignBean.get("SUPPLIER_ACCOUNT"));
                        linkData.set("SUPPLIER_EXPLAIN", assignBean.get("SUPPLIER_EXPLAIN"));
                        linkData.set("S_FLAG", assignBean.get("S_FLAG"));
                        linkData.set("S_USER", assignBean.get("S_USER"));
                        linkData.set("S_DEPT", assignBean.get("S_DEPT"));
                        linkData.set("S_TDEPT", assignBean.get("S_TDEPT"));
                        linkData.set("S_ODEPT", assignBean.get("S_ODEPT"));
                        linkData.set("S_CMPY", assignBean.get("S_CMPY"));
                        linkData.set("S_ATIME", assignBean.get("S_ATIME"));
                        linkData.set("S_MTIME", assignBean.get("S_MTIME"));
                        ServDao.create(BN_HT_SUPPLIER_INFO_LINK, linkData);
                    }
                }
            }
        }
    }


    /**
     * 综合查询
     * @param paramBean 综合查询参数
     */
    @Override
    protected void beforeQuery(ParamBean paramBean) {
        super.beforeQuery(paramBean);

        // 如果是查询的话
        boolean searchFlag = paramBean.getBoolean(ContractMgr.COL_SEARCH_FLAG);
        System.out.println("333");
        if (searchFlag) {
            System.out.println("222");
            paramBean.setQueryExtWhere(ContractMgr.getQuerySql(paramBean));
            System.out.println("111");
        }
    }

    /**
     * 选择范本，复制范本正文到合同里
     * @param paramBean 合同ID和范本ID
     * @return 返回
     */
    public OutBean copyTemplate(ParamBean paramBean) {
        OutBean outBean = new OutBean();
        // 当前节点ID
        String wfNIId = paramBean.getStr("WF_NI_ID");
        // 合同ID
        String ctId = paramBean.getId();
        // 文件ID，如果有值则
        // 取得合同正文  —— 不用覆盖原有文件了 所以不用查找是否有正文
//        Bean fileBean = null;
//        if (StringUtils.isNotBlank(ctId)) {
//            fileBean = ServDao.find(ServMgr.SY_COMM_FILE, new Bean()
//            .set("SERV_ID", ContractMgr.LW_CT_CONTRACT) 
//            .set("FILE_CAT", ContractMgr.FILE_ZHENGWEN).set("DATA_ID", ctId));
//        }

        // 范本ID
        String tpId = paramBean.getStr(ContractMgr.COL_TP_ID);
        // 取得范本正文
        Bean tpFileBean = ServDao.find(ServMgr.SY_COMM_FILE, new Bean()
        .set("SERV_ID", ContractMgr.LW_CT_TEMPLATE) 
        .set("FILE_CAT", ContractMgr.FILE_TP_FILE).set("DATA_ID", tpId));
        if (tpFileBean == null) {
            outBean.setError("该范本没有正文！");
        } else {
            // 复制范本为合同正本
            Bean param = new Bean()
            .set("SERV_ID", ContractMgr.LW_CT_CONTRACT).set("FILE_CAT", ContractMgr.FILE_ZHENGWEN)
            .set("DATA_ID", ctId).set("S_MTIME", DateUtils.getDatetimeTS());
            if (StringUtils.isNotBlank(wfNIId)) {
                param.set("WF_NI_ID", wfNIId);
            }

            // 存在则覆盖
            //上传多个正文文件  不需要覆盖了 所以需要
            //            if (fileBean != null) {
            //                InputStream input;
            //                try {
            //                    input = FileMgr.download(tpFileBean);
            //                    FileMgr.overWrite(fileBean.getId(), input, tpFileBean.getStr("FILE_NAME"), false);
            //                    IOUtils.closeQuietly(input);
            //                } catch (IOException e) {
            //                    log.debug(e.getMessage(), e);
            //                }
            //            } else {
            FileMgr.copyFile(tpFileBean, param);
            //            }
            outBean.setOk("成功选择合同范本！");
        }
        return outBean;
    }

    /**
     * 清稿之前的操作，复制一个文件到修改稿
     * @param paramBean 合同主键ID
     * @return 返回当前正本和复制之后的文件ID
     */
    public OutBean cleanCopy(ParamBean paramBean) {
        String ctId = paramBean.getId();

        // 取得清稿合同对象
        Bean contractBean = ServDao.find(ContractMgr.LW_CT_CONTRACT, new Bean(ctId));

        // 取得当前合同修正版本号
        Integer version = contractBean.getInt(ContractMgr.COL_CT_CVERSION);
        version++;

        // 取得合同正文
        //因为正文可以上传多个了 所以改用finds方法 20150701 ADN
        List<Bean> fileBeans = ServDao.finds(ServMgr.SY_COMM_FILE, new Bean()
        .set("SERV_ID", ContractMgr.LW_CT_CONTRACT) 
        .set("FILE_CAT", ContractMgr.FILE_ZHENGWEN).set("DATA_ID", ctId));


        OutBean outBean = new OutBean();
        outBean.setData(fileBeans);
        for (Bean fileBean:fileBeans) {
            if (fileBean != null) {

                String fileName = fileBean.getStr("FILE_NAME");
                String suffix = fileName.substring(fileName.indexOf("."));
                String newName = fileName.substring(0, fileName.lastIndexOf(".")) + "(第" + version +  "次修改稿)" + suffix;
                String mtype = fileBean.getStr("FILE_MTYPE");
                String disName = fileBean.getStr("DIS_NAME") + "(第" + version +  "次修改稿)";

                //如果是第一次清稿 则不需要找到修改稿的原始文件ID 
                List<Bean> xiugaigaoBeans = null;
                if (version == 1) { 
                    // 更新修改版本号
                    ServDao.update(ContractMgr.LW_CT_CONTRACT, new Bean(ctId).set(ContractMgr.COL_CT_CVERSION, version));

                    //如果是第一次清稿则对所以文件清稿
                    Bean newFile = FileMgr.copyFile(fileBean, new Bean()
                    .set("SERV_ID", ContractMgr.LW_CT_CONTRACT).set("FILE_CAT", ContractMgr.FILE_XIUGAIGAO)
                    .set("DATA_ID", ctId) .set("FILE_NAME", newName).set("FILE_MTYPE", mtype).set("DIS_NAME", disName)
                    .set("S_MTIME", DateUtils.getDatetimeTS()).set("ORIG_FILE_ID", fileBean.getId())
                    .set("S_USER", Context.getUserBean().getCode()).set("S_UNAME", Context.getUserBean().getName())
                    .set("S_DEPT", Context.getUserBean().getDeptCode()).set("S_DNAME", Context.getUserBean().getDeptName())
                    .set("S_CMPY", Context.getUserBean().getCmpyCode()));
                    // 如果清稿失败，前段JS通过该ID删除对应的修改稿文件
                    outBean.set("FILE_COPY_ID", newFile.getId());
                } else {
                    //根据当前正文ID获取对应修改稿列表
                    xiugaigaoBeans = ServDao.finds(ServMgr.SY_COMM_FILE, new Bean()
                    .set("SERV_ID", ContractMgr.LW_CT_CONTRACT) 
                    .set("FILE_CAT", XIUGAIGAO).set("DATA_ID", ctId).set("ORIG_FILE_ID", fileBean.getId()));

                    //获取当前修改稿中的最后更新时间
                    String  maxMTime = "";
                    for (Bean xiugaiBean : xiugaigaoBeans) {
                        if (xiugaiBean != null) {
                            String mTime = xiugaiBean.getStr("S_MTIME");
                            int num = mTime.compareTo(maxMTime);
                            if (num > 0) {
                                maxMTime = mTime;
                            }
                        }
                    }


                    //获取正文的修改时间并转成数字型
                    int zwNum = fileBean.getStr("S_MTIME").compareTo(maxMTime);
                    //如果正文的修改时间大于修改稿中最新的修改时间 则清稿
                    if (maxMTime != null && "" != maxMTime && zwNum > 0) {
                        int xggBb = xiugaigaoBeans.size() +1;
                        String disName2 = fileBean.getStr("DIS_NAME") + "(第" + xggBb +  "次修改稿)";

                        // 更新修改版本号
                        ServDao.update(ContractMgr.LW_CT_CONTRACT, new Bean(ctId).set(ContractMgr.COL_CT_CVERSION, version));
                        // 复制到修改稿
                        Bean newFile = FileMgr.copyFile(fileBean, new Bean()
                        .set("SERV_ID", ContractMgr.LW_CT_CONTRACT).set("FILE_CAT", ContractMgr.FILE_XIUGAIGAO)
                        .set("DATA_ID", ctId) .set("FILE_NAME", newName).set("FILE_MTYPE", mtype).set("DIS_NAME", disName2)
                        .set("S_MTIME", DateUtils.getDatetimeTS()).set("ORIG_FILE_ID", fileBean.getId())
                        .set("S_USER", Context.getUserBean().getCode()).set("S_UNAME", Context.getUserBean().getName())
                        .set("S_DEPT", Context.getUserBean().getDeptCode()).set("S_DNAME", Context.getUserBean().getDeptName())
                        .set("S_CMPY", Context.getUserBean().getCmpyCode()));
                        // 如果清稿失败，前段JS通过该ID删除对应的修改稿文件
                        outBean.set("FILE_COPY_ID", newFile.getId());
                    }
                    //如果是第一次修改则全部清稿
                }
            } else {
                log.error("合同\"" + contractBean.getStr("CT_NAME") + "\"没有正本！");
                outBean.setError("该合同没有正文，请拟制正文！");
            }
        }
        return outBean;
    }

    /**
     * 清稿失败后续处理
     * @param paramBean 合同ID和清稿时复制到修改稿里的文件ID
     * @return 返回
     */
    public OutBean doCleanCopyFailure(ParamBean paramBean) {
        String ctId = paramBean.getId();
        String fileId = paramBean.getStr("FILE_ID");
        int ctCversion = paramBean.getInt(ContractMgr.COL_CT_CVERSION);
        ctCversion--;

        // 更新修改版本号
        ServDao.update(ContractMgr.LW_CT_CONTRACT, new Bean(ctId)
        .set(ContractMgr.COL_CT_CVERSION, ctCversion > 0 ? ctCversion : 0));

        ServMgr.act(new ParamBean().setServId(ServMgr.SY_COMM_FILE).setAct(ServMgr.ACT_DELETE).setId(fileId));

        return new OutBean();
    }

    /**
     * 保存权限
     * @param paramBean 参数
     * @return 返回
     */
    public OutBean saveAuthority(ParamBean paramBean) {
        OutBean outBean = new OutBean();
        List<Bean> authoritys = JsonUtils.toBeanList(paramBean.getStr("BATCHDATAS"));
        Iterator<Bean> it = authoritys.iterator();
        while (it.hasNext()) {
            Bean authority = it.next();
            String ctId = authority.getStr("CT_ID");
            String userId = authority.getStr("AUTH_OWNER");
            if (!ContractMgr.isAuthOwner(new Bean().setId(ctId), userId)) {
                ServDao.create(ContractMgr.LW_CT_AUTHORITY, authority);
            }
        }
        return outBean.setOk("成功授权");
    }


    /**
     * 判断文件是否已经锁定
     * @param paramBean 参数
     * @return 是否已锁定
     */
    public OutBean  isLocked(ParamBean paramBean) {
        ParamBean procBean = new ParamBean();
        procBean.setId(paramBean.getStr("S_WF_INST"));
        Bean instBean = ServDao.find("SY_WFE_PROC_INST", procBean);
        return new OutBean().set("INST_LOCK" , instBean.getInt("INST_LOCK"));
    }
    
}
