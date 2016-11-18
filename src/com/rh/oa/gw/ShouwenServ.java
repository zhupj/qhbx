package com.rh.oa.gw;

import com.rh.core.base.Bean;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.oa.gw.util.GwConstant;
import com.rh.oa.gw.util.GwUtils;

/**
 * 收文服务的实现类
 * @author yangjy
 * 
 */
public class ShouwenServ extends GwServ {

    @Override
    protected void beforeByid(ParamBean paramBean) {
        super.beforeByid(paramBean);
        if (!paramBean.getAddFlag() && paramBean.getId().length() > 0) {
            // 增加是否存在PDF盖章文件参数
            GwSealProcess gwSeal = GwUtils.createGwSeal();

            Bean gwParamBean = getGwParamBean(paramBean);
            if (gwSeal.hasSealed(paramBean)) {
                gwParamBean.set(GwConstant.EXIST_SEAL_PDF_FILE, "true");
            }
            
            paramBean.set(GwConstant.GW_PARAM, gwParamBean);
        }
    }
    
    @Override
    protected void afterByid(ParamBean paramBean, OutBean outBean) {
        super.afterByid(paramBean, outBean);
        if (paramBean.containsKey(GwConstant.GW_PARAM)) {
            Bean gwParamBean = paramBean.getBean(GwConstant.GW_PARAM);
            outBean.copyFrom(gwParamBean);
        }
    }

    
    /** 流经表ID **/
    public static final String SY_SERV_FLOW = "SY_SERV_FLOW";
    /** 公文表 **/
    public static final String OA_GW_GONGWEN = "OA_GW_GONGWEN";
    /**
     * 导入Excel
     * @param paramBean 参数信息
     * @return 执行结果
     */
    public OutBean imp(ParamBean paramBean) {
        //@todo 调用父类的imp导入方法
        OutBean outBean = super.imp(paramBean);
        
        //获取导入的数据
        String[] saveIds = outBean.getSaveIds().split(",");
        
        for (String saveId:saveIds) {
            Bean shouwenBean = ServDao.find(paramBean.getServId(), saveId);
            //设置模板编码为保监会收文
            shouwenBean.set("TMPL_CODE", "OA_GW_TYPE_SW_BJH");
            //向保监会收文表中S_UNAME字段插入当前登陆人的登录名
            Bean userBean = ServDao.find("SY_ORG_USER",  shouwenBean.getStr("S_USER"));
            shouwenBean.set("S_UNAME", userBean.getStr("USER_NAME"));
            ServDao.update(OA_GW_GONGWEN, shouwenBean);
            //像流经表中插入数据 --OWNER_ID
            Bean flowBean = new Bean();
            flowBean.set("DATA_ID", shouwenBean.get("GW_ID"));
            flowBean.set("OWNER_ID", shouwenBean.get("S_ODEPT"));
            flowBean.set("S_ODEPT", shouwenBean.get("S_ODEPT"));
            flowBean.set("FLOW_FLAG", "1");
            ServDao.create(SY_SERV_FLOW, flowBean);
        }
        return outBean;
    }
}