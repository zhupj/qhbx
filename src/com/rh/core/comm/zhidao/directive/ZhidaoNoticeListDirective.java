package com.rh.core.comm.zhidao.directive;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.base.Bean;
import com.rh.core.comm.cms.directive.DirectiveUtils;
import com.rh.core.comm.zhidao.ZhidaoServ;
import com.rh.core.serv.ParamBean;

import freemarker.core.Environment;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * @author liwei 公告
 */
public class ZhidaoNoticeListDirective implements TemplateDirectiveModel {
    /** log */
    private static Log log = LogFactory.getLog(ZhidaoNoticeListDirective.class);

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void execute(Environment env, Map params,
            TemplateModel[] loopVars, TemplateDirectiveBody body)
            throws TemplateException, IOException {
        String debugName = DirectiveUtils.getString("debugName", params);
        int count = DirectiveUtils.getInt("count", params);
        boolean needMore = Boolean.parseBoolean(DirectiveUtils.getString("needMore", params));

        log.debug(debugName + ":开始");
        ParamBean param = new ParamBean();
        param.set("count", count);
        param.set("needMore", needMore);
        List<Bean> list = new ZhidaoServ().getZhidaoNoticeList(param).getDataList();

        Map<String, TemplateModel> paramWrap = new HashMap<String, TemplateModel>(params);
        paramWrap.put("tag_list", ObjectWrapper.DEFAULT_WRAPPER.wrap(list));
        Map<String, TemplateModel> origMap = DirectiveUtils.addParamsToVariable(env, paramWrap);
        if (body != null) {
            body.render(env.getOut());
        }
        DirectiveUtils.removeParamsFromVariable(env, paramWrap, origMap);
        log.debug(debugName + ":结束");

    }
}