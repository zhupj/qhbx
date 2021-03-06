package com.rh.core.comm.zhidao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.comm.cms.mgr.ReqhisMgr;
import com.rh.core.comm.event.mgr.EventMgr;
import com.rh.core.comm.follow.FollowServ;
import com.rh.core.comm.integral.IntegralMgr;
import com.rh.core.comm.integral.IntegralServ;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.UserMgr;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;
import com.rh.core.serv.dict.DictMgr;
import com.rh.core.util.Constant;
import com.rh.core.util.DateUtils;
import com.rh.core.util.Lang;

/**
 * 知道虚拟服务 用于提供知道页面的数据
 * @author liwei
 * 
 */
public class ZhidaoServ extends CommonServ {
    /** 知道服务名称 */
    public static final String SY_COMM_ZHIDAO = "SY_COMM_ZHIDAO";

    /**
     * 获取用户知道信息
     * @param paramBean - 参数bean
     * @return outBean - 输出bean
     */
    public OutBean getMyInfo(ParamBean paramBean) {
        UserBean currentUser = Context.getUserBean();
        // get integral
        int integral = IntegralMgr.getInstance().getUserIntegral(SY_COMM_ZHIDAO, currentUser.getCode());

        // get my question count
        ParamBean quesQuery = new ParamBean();
        quesQuery.setServId(ServMgr.SY_COMM_ZHIDAO_QUESTION);
        quesQuery.setAct(ServMgr.ACT_COUNT);
        quesQuery.set("S_USER", currentUser.getCode());
        int quesCounter = ServMgr.act(quesQuery).getCount();

        // get my questions
        ParamBean quesListQuery = new ParamBean();
        quesListQuery.setServId("SY_COMM_ZHIDAO_MYASK");
        quesListQuery.setAct(ServMgr.ACT_QUERY);
        quesListQuery.set("S_USER", currentUser.getCode());
        quesListQuery.setShowNum(5);
        quesListQuery.setOrder("S_MTIME DESC");
        List<Bean> quesList = ServMgr.act(quesListQuery).getDataList();

        // get my answer count
        ParamBean answQuery = new ParamBean();
        answQuery.setServId(ServMgr.SY_COMM_ZHIDAO_ANSWER);
        answQuery.setAct(ServMgr.ACT_COUNT);
        answQuery.set("S_USER", currentUser.getCode());
        int anwsCounter = ServMgr.act(answQuery).getCount();

        // get my answers
        ParamBean answListQuery = new ParamBean();
        answListQuery.setServId(ServMgr.SY_COMM_ZHIDAO_ANSWER);
        answListQuery.set("S_USER", currentUser.getCode());
        answListQuery.setAct(ServMgr.ACT_QUERY);
        answListQuery.setShowNum(5);
        answListQuery.setOrder("S_MTIME DESC");
        List<Bean> anwsList = ServMgr.act(answListQuery).getDataList();

        OutBean outBean = new OutBean();
        outBean.set("USER", currentUser);
        outBean.set("USER_INTEGRAL", integral);
        outBean.set("QUESTION_COUNT", quesCounter);
        outBean.set("QUESTION_LIST", quesList);
        outBean.set("ANSWER_COUNT", anwsCounter);
        outBean.set("ANSWER_LIST", anwsList);

        // 增加积分
        String key = "SY_COMM_ZHIDAO_VISIT_" + DateUtils.getDate();
        IntegralMgr.getInstance().handle(currentUser.getCode(), ZhidaoServ.SY_COMM_ZHIDAO, ZhidaoServ.SY_COMM_ZHIDAO,
                key, "每日登录", "SY_COMM_ZHIDAO_VISIT");
        return outBean;
    }

    /**
     * 获取用户首页中关于"我关注的人""我关注的问题""我提出的问题"的信息
     * @param paramBean - 参数Bean
     * @return - 输出Bean
     */
    public OutBean getMyIndexInfo(ParamBean paramBean) {
        UserBean currentUser = Context.getUserBean();
        // 获取我关注的人

        // 获取我关注的问题
        ParamBean myFollowQuery = new ParamBean();
        myFollowQuery.setServId("SY_COMM_ZHIDAO_MYFOLLOW");
        myFollowQuery.set("S_USER", currentUser.getCode());
        myFollowQuery.setAct(ServMgr.ACT_QUERY);
        myFollowQuery.setShowNum(5);
        myFollowQuery.setOrder("S_MTIME DESC");
        List<Bean> myFollowList = ServMgr.act(myFollowQuery).getDataList();
        // 获取我提出的问题
        ParamBean myAskQuery = new ParamBean();
        myAskQuery.setServId("SY_COMM_ZHIDAO_MYASK");
        myAskQuery.set("S_USER", currentUser.getCode());
        myAskQuery.setLinkFlag(true);
        myAskQuery.setAct(ServMgr.ACT_FINDS);
        myAskQuery.setShowNum(5);
        myAskQuery.setOrder("S_MTIME DESC");
        List<Bean> myAskList = ServMgr.act(myAskQuery).getDataList();

        OutBean outBean = new OutBean();
        outBean.set("USER", currentUser);
        outBean.set("MY_FOLLOW_LIST", myFollowList);
        outBean.set("MY_ASK_LIST", myAskList);
        return outBean;
    }

    /**
     * 获取对回答投支持票用户
     * @param paramBean - 参数bean
     * @return - outBean
     */
    public OutBean getAnswerLikeVote(ParamBean paramBean) {
        String answerId = paramBean.getStr("answerId");
        int likeVoteType = 1;
        return getAnswerVoteUser(answerId, likeVoteType);
    }

    /**
     * 获取知道提问列表
     * @param paramBean - 参数bean
     * @return - outbean
     */
    public OutBean getAskList(ParamBean paramBean) {
        String siteId = paramBean.getStr("siteId");
        String chnlId = paramBean.getStr("chnlId");
        int page = paramBean.getInt("page");
        String userId = paramBean.getStr("userId");
        int count = paramBean.getInt("count");
        String order = paramBean.getStr("order");
        // 查询的是否是零回答的数据
        String noAnswer = paramBean.getStr("noAnswer");

        // 是否统计followCount
        String isFollowCount = paramBean.getStr("isFollowCount");

        if (0 == page) {
            page = 1;
        }
        ParamBean param = new ParamBean(ServMgr.SY_COMM_ZHIDAO_QUESTION, ServMgr.ACT_QUERY);

        if (order != null && order.length() > 0) {
            param.setOrder(order);
        } else {
            param.setOrder(" S_MTIME DESC ");
        }

        String where = "";
        if (siteId != null && siteId.length() > 0) {
            where += " AND SITE_ID ='" + siteId + "' ";
        }

        if (userId != null && userId.length() > 0) {
            where += " and S_USER='" + userId + "'";
        }
        if (noAnswer != null && noAnswer.length() > 0) {
            where += " and Q_ANSWER_COUNTER = 0";
        }

        if (0 < where.length()) {
            param.setQueryExtWhere(where);
        }
        if (count > 0) {
            param.setQueryPageShowNum(count);
        } else {
            param.setQueryPageShowNum(10);
        }

        if (page > 0) {
            param.setQueryPageNowPage(page);
        }

        if (null != chnlId && 0 < chnlId.length()) {
            List<Bean> treeWhere = new ArrayList<Bean>();
            treeWhere.add(new Bean().set("DICT_ITEM", "CHNL_ID").set("DICT_VALUE", chnlId));
            param.set("_treeWhere", treeWhere);
        }
        OutBean outBean = ServMgr.act(param);
        // TODO 前端计算
        // timeago format
        if (isFollowCount.equals("isFollowCount")) {
            List<Bean> dataList = outBean.getDataList();
            ParamBean followQuery = new ParamBean();
            // TODO 前端计算
            // timeago format
            for (Bean b : dataList) {
                // String timeStr = b.getStr("S_MTIME");
                // if (0 == timeStr.length()) {
                // continue;
                // }
                // Date d = DateUtils.getDateFromString(timeStr);
                // String timeAgo = DateUtils.timeAgo(d);
                // b.set("S_MTIME_TIMEAGO", timeAgo);
                followQuery.set("questionId", b.getStr("Q_ID"));
                b.set("quesfollowCounter", getAskFollowCount(followQuery).getCount());
            }
        }

        return outBean;
    }

    /**
     * 获取邀请我回答的问题的列表
     * @param paramBean - 传入参数
     * @return - 问题列表
     */
    public OutBean getInviteMeAskList(ParamBean paramBean) {
        // 获取当前用户
        UserBean currentUser = Context.getUserBean();
        String userId = currentUser.getCode();
        // 查询的是否是零回答的数据
        String noAnswer = paramBean.getStr("noAnswer");

        // 构造Q_ID的字符串查询条件
        ParamBean inviteQuery = new ParamBean();
        inviteQuery.set("TARGET_USER", userId);
        inviteQuery.setServId(ServMgr.SY_COMM_ZHIDAO_INVITE);
        inviteQuery.setAct(ServMgr.ACT_FINDS);
        List<Bean> inviteList = ServMgr.act(inviteQuery).getDataList();
        StringBuffer qIds = new StringBuffer();
        qIds.append("(");
        for (Bean bean : inviteList) {
            qIds.append("'");
            qIds.append(bean.getStr("Q_ID"));
            qIds.append("',");
        }
        String qId = qIds.substring(0, qIds.length() - 1) + ")";

        // 查询问题
        ParamBean questionQuery = new ParamBean(ServMgr.SY_COMM_ZHIDAO_QUESTION, ServMgr.ACT_QUERY);
        questionQuery.setOrder(" S_MTIME DESC ");
        questionQuery.setQueryPageShowNum(10);
        questionQuery.setQueryPageNowPage(1);
        String where = "";
        if (noAnswer != null && noAnswer.length() > 0) {
            where += " and Q_ANSWER_COUNTER = 0";
        }
        if (qId.length() > 1) {
            where += " and Q_ID in " + qId;
        } else {
            where += "and Q_ID in ('0','1')";
        }
        questionQuery.setQueryExtWhere(where);
        OutBean outBean = ServMgr.act(questionQuery);

        return outBean;
    }

    
    /**
     * 获取收藏的列表
     * @param param - 参数
     * @return - 返回值
     */
    public OutBean getFavoritesList(ParamBean param) {
        int page = param.getInt("page");
        String userId = param.getStr("userId");
        int count = param.getInt("count");
        String order = param.getStr("order");
        ParamBean query = new ParamBean();
        query.setQueryExtWhere(" and SERV_ID = 'SY_COMM_ZHIDAO_QUESTION' and S_USER = '" + userId + "'");
        query.setQueryPageNowPage(page);
        query.setQueryPageShowNum(count);
        if (order != "" && order != null) {
        	query.setQueryPageOrder(order);
        } else {
        	query.setQueryPageOrder("S_ATIME desc");
        }
        OutBean outBean = ServMgr.act(ServMgr.SY_COMM_FAVORITES, ServMgr.ACT_QUERY, query);
        List<Bean> favoritesList = outBean.getDataList();
        if (favoritesList.size() > 0) {
        	for (int i = 0; i < favoritesList.size(); i++) {
				String dataId = favoritesList.get(i).getStr("DATA_ID");
				favoritesList.get(i)
						.set("question",
								ServMgr.act(ServMgr.SY_COMM_ZHIDAO_QUESTION,
										ServMgr.ACT_BYID,
										new ParamBean().setId(dataId)));
				System.out.println("-----------------------"+favoritesList.get(i).toString()+"\n");
			}
        }
    	return outBean;
    }
    
    /**
     * 统计收藏数量
     * @param paramBean - 参数bean
     * @return - 结果bean
     */
    public OutBean getFavoriteCount(ParamBean paramBean) {
    	String userId = paramBean.getStr("userId");
    	
    	ParamBean favoriteParam = new ParamBean();
    	favoriteParam.setServId(ServMgr.SY_COMM_FAVORITES);
    	favoriteParam.setAct(ServMgr.ACT_COUNT);
    	if (userId != null && userId.length() > 0) {
    		favoriteParam.set("S_USER", userId);
    	}
    	favoriteParam.set("SERV_ID", "SY_COMM_ZHIDAO_QUESTION");
    	return ServMgr.act(favoriteParam);
    }
    
    
    
    /**
     * 获取知道推荐列表
     * @param paramBean - 参数bean
     * @return - outbean
     */
    public OutBean getRecommendList(ParamBean paramBean) {
        // 用户ID
        String userId = Context.getUserBean().getCode();
        int count = paramBean.getInt("count");
        count = count > 0 ? count : 10;

        // 如果pageName等于myfollow,说明是"我的关注"页面用到了这个宏
        String pageName = paramBean.getStr("pageName");

        // 1. 我提出的问题 QuestionServ
        ParamBean myQuestParam = new ParamBean(ServMgr.SY_COMM_ZHIDAO_QUESTION, ServMgr.ACT_QUERY);
        if ("myfollow".equals(pageName)) {
            myQuestParam.setQueryNoPageFlag(true);
            myQuestParam.setQueryExtWhere(" and S_USER='" + userId + "'").setOrder("S_MTIME DESC");
        } else {
            myQuestParam.setQueryExtWhere(" and S_USER='" + userId + "'").setOrder("S_MTIME DESC").setShowNum(count);
        }
        List<Bean> myAskList = ServMgr.act(myQuestParam).getDataList();

        // 2. 我关注的问题
        ParamBean myFollowQestParam = new ParamBean(ServMgr.SY_COMM_ZHIDAO_MY_QUESTION_FOLLOW, ServMgr.ACT_QUERY);
        if ("myfollow".equals(pageName)) {
            myFollowQestParam.setQueryNoPageFlag(true);
            myFollowQestParam.set("S_USER", userId).setOrder("S_MTIME DESC");
        } else {
            myFollowQestParam.set("S_USER", userId).setShowNum(count).setOrder("S_MTIME DESC");
        }
        List<Bean> myQusetFollowList = ServMgr.act(myFollowQestParam).getDataList();

        // 3. 我关注的人
        ParamBean myFollowPearsonQuery = new ParamBean();
        myFollowPearsonQuery.set("count", count);
        if (pageName != "") {
            myFollowPearsonQuery.set("pageName", pageName);
        }
        List<Bean> myFollowPersonList = getMyFollowUserInfo(myFollowPearsonQuery).getDataList();
        // 获取关注的人最新动态
        ParamBean tempParam = new ParamBean();
        for (Bean bean : myFollowPersonList) {
            tempParam.set("userId", bean.getStr("DATA_ID"));
            tempParam.set("count", 1);
            List<Bean> list = getUserActivity(tempParam).getDataList();
            for (Bean tempBean : list) {
                bean.set("ACT_CODE__NAME", tempBean.getStr("ACT_CODE__NAME"));
                bean.set("ACT_CODE", tempBean.getStr("ACT_CODE"));
                if (tempBean.getStr("ACT_CODE").equals("ZHIDAO_CREATE_ANSWER")
                        || tempBean.getStr("ACT_CODE").equals("ZHIDAO_LIKE_ANSWER")) {
                    // 如果 回答 和赞同，则取 Q_ID 和 Q_TITLE
                    Bean answer = (Bean) tempBean.get("answer");
                    bean.set("ACTIVITY_DATA_ID", answer.getStr("Q_ID"));
                    bean.set("ACTIVITY_DATA_DIS_NAME", answer.getStr("Q_TITLE"));
                } else {
                    bean.set("ACTIVITY_DATA_ID", tempBean.getStr("DATA_ID"));
                    bean.set("ACTIVITY_DATA_DIS_NAME", tempBean.getStr("DATA_DIS_NAME"));
                }
            }
        }

        // 4. 我关注的分类
        ParamBean categoryParam = new ParamBean(ServMgr.SY_COMM_ZHIDAO_MY_CATEGORY_FOLLOW, ServMgr.ACT_QUERY);
        categoryParam.set("S_USER", userId).setOrder("S_ATIME DESC").setShowNum(count);
        List<Bean> myCategoryList = ServMgr.act(categoryParam).getDataList();

        OutBean outBean = new OutBean();
        outBean.set("myAskList", myAskList);
        outBean.set("myQusetFollowList", myQusetFollowList);
        outBean.set("myFollowPersonList", myFollowPersonList);
        outBean.set("myCategoryList", myCategoryList);
        return outBean;
    }

    /**
     * 根据问题Id查询出最优答案
     * @param paramBean - 问题Id
     * @return - 最佳答案
     */
    public OutBean getAnswer(ParamBean paramBean) {
        String qId = paramBean.getStr("Q_ID");
        int summaryLength = paramBean.getInt("summaryLength");
        ParamBean query = new ParamBean();
        query.set("id", qId);
        query.set("count", 1);
        query.set("page", 1);
        query.set("summaryLength", summaryLength);
        List<Bean> dataList = getAnswerList(query).getDataList();
        Bean data = new Bean();
        if (null != dataList && 0 < dataList.size()) {
            data = dataList.get(0);
        }
        return new OutBean().set("bestAnswer", data);
    }

    /**
     * 根据问题的Id查询出问题的详细内容和系统推荐的最优答案
     * @param param - 问题id
     * @return - 问题详情和最优答案
     */
    public OutBean getQuestionAndBestAnswerByQId(ParamBean param) {
        String qId = param.getStr("Q_ID");
        int summaryLength = param.getInt("summaryLength");
        OutBean questionOutBean = new QuestionServ().byid(new ParamBean().setId(qId));
        OutBean answerOutBean = getAnswer(new ParamBean().set("Q_ID", qId).set("summaryLength", summaryLength));
        return new OutBean().set("question", questionOutBean).set("answer", answerOutBean);
    }

    /**
     * 获取知道回答列表
     * @param paramBean - 参数bean
     * @return - outbean
     */
    public OutBean getAnswerList(ParamBean paramBean) {
        // ask id
        String id = paramBean.getStr("id");
        String userId = paramBean.getStr("userId");
        String sort = paramBean.getStr("sort");
        int count = paramBean.getInt("count");
        int page = paramBean.getInt("page");
        int summaryLength = paramBean.getInt("summaryLength");

        ParamBean answQuery = new ParamBean(ServMgr.SY_COMM_ZHIDAO_ANSWER, ServMgr.ACT_QUERY);
        if (id != null && id.length() > 0) {
            answQuery.setQuerySearchWhere(" and Q_ID='" + id + "'");
        }
        if (userId != null && userId.length() > 0) {
            answQuery.setQueryExtWhere(" and S_USER='" + userId + "'");
        }
        if (sort != null && sort.length() > 0) {
            answQuery.setOrder(sort);
        } else {
            answQuery.setOrder(" A_BEST DESC,A_ADMIN_BEST DESC,A_LIKE_VOTE DESC");
        }

        if (0 < page) {
            answQuery.setQueryPageNowPage(page);
        }

        if (0 < count) {
            answQuery.setQueryPageShowNum(count);
        } else {
            answQuery.setQueryPageShowNum(30);
        }
        OutBean outBean = ServMgr.act(answQuery);
        List<Bean> dataList = outBean.getDataList();
        for (Bean data : dataList) {
            if (0 < summaryLength) {
                String text = data.getStr("A_CONTENT");
                // 标识是否显示 阅读更多
                data.set("readMore", text.length() > summaryLength);
                text = Lang.getSummary(text, summaryLength);
                data.set("A_CONTENT", text);
            }

            // 判断是否有答案被选为管理员满意答案
            String adminBest = data.getStr("A_ADMIN_BEST");
            // 如果是已经被管理员选定为最佳答案
            if ("1".equals(adminBest)) {
                outBean.set("isAdminBest", "1");
            }
        }
        outBean.set("isAdminRole", Context.getUserBean().isAdminRole());
        if (outBean.getStr("isAdminBest").equals("")) {
            outBean.set("isAdminBest", "2");
        }
        return outBean;
    }

    /**
     * 获取最近问答
     * @param paramBean - 参数bean
     * @return - outbean
     */
    public OutBean getQaList(ParamBean paramBean) {
        int page = paramBean.getInt("page");
        page = page == 0 ? 1 : page;
        int count = paramBean.getInt("count");
        count = count == 0 ? 10 : count;
        // 知道专家类型 1:特邀专家 2:职能部门 3:业务专家
        int specType = paramBean.getInt("specType");
        // 获取回答数
        int answerCount = paramBean.getInt("answerCount");
        // 回答摘要长度
        int summaryLength = paramBean.getInt("summaryLength");

        ParamBean param = new ParamBean("SY_COMM_ZHIDAO_RECOMMEND", ServMgr.ACT_QUERY);
        param.setQueryPageShowNum(count);
        param.setQueryPageNowPage(page);
        param.setOrder(" S_MTIME DESC ");
        if (specType > 0) {
            param.setQueryExtWhere(" AND SPEC_TYPE='" + specType + "'");
        }
        OutBean outBean = ServMgr.act(param);
        List<Bean> dataList = outBean.getDataList();
        ParamBean tempParam = new ParamBean();
        tempParam.set("count", answerCount);
        tempParam.set("summaryLength", summaryLength);

        for (Bean b : dataList) {
            String askId = b.getStr("Q_ID");
            // 获取问题相关评论数
            OutBean askBean = new QuestionServ().byid(new ParamBean().setId(askId));
            b.set("ask", askBean);

            // 获取回答
            tempParam.set("id", askId);
            OutBean answerBean = getAnswerList(tempParam);
            List<Bean> list = answerBean.getDataList();
            if (list.size() > 0) {
                b.set("answer", list.get(0));
            }
        }

        return outBean;
    }

    /**
     * 获知道公告
     * @param paramBean - 参数bean
     * @return - out bean
     */
    public OutBean getZhidaoNotice(ParamBean paramBean) {
        String id = paramBean.getId();

        ParamBean param = new ParamBean(ServMgr.SY_COMM_ZHIDAO_NOTICE, ServMgr.ACT_BYID);
        param.setOrder("S_MTIME DESC");
        param.setId(id);
        OutBean outBean = ServMgr.act(param);
        return outBean;
    }

    /**
     * 获取知道公告列表
     * @param paramBean - 参数bean
     * @return - 结果bean
     */
    public OutBean getZhidaoNoticeList(ParamBean paramBean) {
        boolean needMore = paramBean.getBoolean("needMore");
        int count = paramBean.getInt("count");

        // TODO query
        ParamBean param = new ParamBean();
        param.set("SERV_ID", ZhidaoServ.SY_COMM_ZHIDAO);
        param.setShowNum(count);
        param.setOrder("S_MTIME DESC");
        List<Bean> list = ServDao.finds(ServMgr.SY_COMM_ZHIDAO_NOTICE, param);
        // OutBean outbean=ServMgr.act(ServMgr.SY_COMM_WENKU_NOTICE, ServMgr.ACT_QUERY, param);
        // List<Bean> list=outbean.getDataList();
        if (needMore) { // 用于公告阅读 取出用户头像 用户所属部门
            // 获取用户信息
            for (Bean b : list) {
                UserBean user = UserMgr.getUser(b.getStr("S_USER"));
                b.set("DEPT_NAME", user.get("DEPT_NAME"));
                b.set("USER_IMG", user.get("USER_IMG"));
            }
        }
        return new OutBean().setData(list);
    }

    /**
     * 根据一个专家的userId来查找这个专家的领域
     * @param paramBean - 参数bean
     * @return - outbean
     */
    public OutBean getSpecialistSubject(ParamBean paramBean) {
        String userId = paramBean.getStr("userId");

        // 取出当前专家擅长的栏目
        ParamBean specialistSubject = new ParamBean(ServMgr.SY_COMM_ZHIDAO_SPEC_SUBJECT, ServMgr.ACT_QUERY);
        String where = "";
        if (null != userId && 0 < userId.length()) {
            where += " AND SPEC_ID='" + userId + "'";
        }

        if (0 < where.length()) {
            specialistSubject.setQueryExtWhere(where);
        }
        return ServMgr.act(specialistSubject);
    }

    /**
     * 获取专家总的回答量/上周回答量/擅长的领域(是一个用逗号隔开的字符串)/专家详细信息的集合
     * @param paramBean - count显示条数/channelId栏目Id/order排序字符串
     * @return - outBean
     */
    public OutBean getSpecialistInfo(ParamBean paramBean) {
        int count = paramBean.getInt("count");
        String channelId = paramBean.getStr("channelId");
        String order = paramBean.getStr("order");

        // 获取擅长这个栏目的专家
        ParamBean goodBean = new ParamBean();
        if (channelId != null && channelId.length() > 0) {
            goodBean.set("chnlId", channelId);
        }
        // 获取按顺序的前几条专家信息
        if (order != null && order.length() > 0) {
            goodBean.set("order", order);
        } else {
            goodBean.set("order", "SPEC_SORT");
        }

        // 设置查询条数
        if (count > 0) {
            goodBean.set("showNum", count);
        } else {
            goodBean.set("showNum", 15);
        }
        OutBean tempBean = getChnlSpecialist(goodBean);
        List<Bean> list = tempBean.getDataList();
        tempBean.getPage();

        // 声明返回值
        List<Bean> result = new ArrayList<Bean>();

        // 遍历查询出来的专家userId
        for (Bean b : list) {
            String userId = b.getStr("USER_ID");

            // //将用户的头像统一到USER_IMG字段上，方便前台宏调用
            // b.set("USER_IMG", b.getStr("USER_ID__IMG"));

            // 取出当前人的回答总数
            ParamBean answerNum = new ParamBean();
            answerNum.set("userId", userId);
            Bean answerBean = getAnswerCount(answerNum);

            // 取出上周当前人的回答总数
            ParamBean lastWeekAnswerNum = new ParamBean();
            lastWeekAnswerNum.set("userId", userId);
            Bean lastWeekAnswerBean = getLastWeekAskCount(lastWeekAnswerNum);

            // 获取当前专家擅长的栏目
            ParamBean specialistSubject = new ParamBean();
            specialistSubject.set("userId", userId);
            List<Bean> subList = getSpecialistSubject(specialistSubject).getDataList();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < subList.size(); i++) {
                String chnl = DictMgr.getName("SY_COMM_ZHIDAO_CHNL_MANAGE", subList.get(i).getStr("CHNL_ID"));
                if (i == subList.size() - 1) {
                    sb.append(chnl);
                } else {
                    sb.append(chnl + ",");
                }
            }

            // 获取当前专家的详细信息
            Bean specInfoBean = UserMgr.getUser(userId);

            // 添加值
            specInfoBean.set("chnl", sb);
            specInfoBean.set("SPEC_SUB", sb);
            specInfoBean.set("allCount", answerBean.getStr("_DATA_"));
            specInfoBean.set("A_COUNTER", lastWeekAnswerBean.getStr("_DATA_"));
            specInfoBean.set("_OKCOUNT_", lastWeekAnswerBean.getStr("_DATA_"));
            specInfoBean.set("SPEC_TYPE", subList.get(0).getStr("SPEC_TYPE"));
            result.add(specInfoBean);
        }
        // list.clear();
        OutBean outBean = new OutBean();
        outBean.setData(result);
        return outBean;
    }

    /**
     * 专家分类中用到的专家的信息
     * @param param - 传入参数
     * @return - 返回结果
     */
    public OutBean getSpecialistDetail(ParamBean param) {
        int summaryLength = param.getInt("summaryLength");
        // 根据chnlId按照分页查询专家userID
        OutBean outBean = getChnlSpecialist(param);
        outBean.getPage();

        // 获得专家userId
        List<Bean> list = outBean.getDataList();

        for (int j = 0; j < list.size(); j++) {
            String userId = list.get(j).getStr("USER_ID");

            try {
                list.get(j).set("USER_INFO", UserMgr.getUser(userId));
            } catch (Exception e) {
                System.out.println("这个用户ID抛出异常了！" + userId);
                list.remove(j);
                continue;
            }

            // 根据专家userId来获取这个专家的领域
            ParamBean specialistSubject = new ParamBean();
            specialistSubject.set("userId", userId);
            OutBean specBean = getSpecialistSubject(specialistSubject);
            List<Bean> specListBean = specBean.getDataList();
            // 获取专家类别
            // list.get(j).set("SPEC_TYPE", specListBean.get(0).getStr("SPEC_TYPE"));
            StringBuffer speclists = new StringBuffer();
            for (int i = 0; i < specListBean.size(); i++) {
                Bean spectBean = specListBean.get(i);
                String chnlName = DictMgr.getFullName("SY_COMM_ZHIDAO_CHNL_MANAGE", spectBean.getStr("CHNL_ID"));
                if (i == specListBean.size() - 1) {
                    speclists.append(chnlName);
                } else {
                    speclists.append(chnlName);
                    speclists.append(",");
                }
            }

            // 根据专家userId来获取这个专家上周回答问题个数
            ParamBean querySpecBean = new ParamBean();
            querySpecBean.set("userId", userId);
            OutBean questionOutBean = getLastWeekAskCount(querySpecBean);

            // 根据专家userId获取专家最新的问题和回答
            ParamBean tempQuery = new ParamBean();
            tempQuery.set("DATA_ID", userId);
            tempQuery.set("summaryLength", summaryLength);
            OutBean newQuestionAnswer = new OutBean();
            newQuestionAnswer = getNewQuestionAnswerByPerson(tempQuery);

            // 根据专家userId来获取这个专家所有回答中赞同最高的一个回答
            // ParamBean answerBean = new ParamBean();
            // answerBean.setServId("SY_COMM_ZHIDAO_ANSWER");
            // answerBean.setAct(ServMgr.ACT_QUERY);
            // answerBean.setQueryExtWhere(" and S_USER='" + userId + "'");
            // answerBean.setOrder(" A_LIKE_VOTE DESC");
            // answerBean.setQueryPageShowNum(1);
            // List<Bean> temp = ServMgr.act(answerBean).getDataList();
            // Bean answer = null;
            // if (temp.size() > 0) {
            // answer = (Bean) temp.get(0);
            // if (0 < summaryLength) {
            // String text = answer.getStr("A_CONTENT");
            // text = Lang.getSummary(text, summaryLength);
            // answer.set("A_CONTENT", text);
            // }
            // }
            list.get(j).set("_OKCOUNT_", questionOutBean.getStr("_OKCOUNT_"));
            list.get(j).set("SPEC_SUB", speclists);
            list.get(j).set("q_and_a", newQuestionAnswer);
            // list.get(j).set("ANSWER", answer);

            // 查询出这个人的最新动态5条记录
            List<Bean> activityList = getUserActivity(
                    new ParamBean().set("userId", userId).set("summaryLength", summaryLength).set("count", 5))
                    .getDataList();
            list.get(j).set("activityList", activityList);

        }
        outBean.setData(list);
        return outBean;
    }

    /**
     * 跟据userId查询专家信息
     * @param paramBean - userId
     * @return - 专家信息
     */
    public OutBean getSpecialist(ParamBean paramBean) {
        String userId = paramBean.getStr("userId");
        ParamBean query = new ParamBean();
        query.setServId(ServMgr.SY_COMM_ZHIDAO_SPECIALIST);
        query.setAct(ServMgr.ACT_FINDS);
        query.set("USER_ID", userId);
        return ServMgr.act(query);
    }

    /**
     * 根据专家分类chnlId获取专家userId
     * @param paramBean - 参数专家chnlId,显示条数，当前页数
     * @return - 擅长这个分类的专家userId集合
     */
    public OutBean getChnlSpecialist(ParamBean paramBean) {
        String chnlId = paramBean.getStr("chnlId");
        int showNum = paramBean.getInt("showNum");
        int curPage = paramBean.getInt("curPage");
        String order = paramBean.getStr("order");

        String servId = "";
        ParamBean queryBean = new ParamBean();
        queryBean.setAct(ServMgr.ACT_QUERY);
        if (chnlId != null && chnlId.length() > 0) {
            servId = ServMgr.SY_COMM_ZHIDAO_SPEC_SUBJECT;
            // queryBean.setQueryExtWhere(" and CHNL_ID='" + chnlId + "' or CHNL_PID='"
            // + chnlId + "'");
            List<Bean> treeWhere = new ArrayList<Bean>();
            treeWhere.add(new Bean().set("DICT_ITEM", "CHNL_ID").set("DICT_VALUE", chnlId));
            queryBean.set("_treeWhere", treeWhere);
        } else {
            servId = ServMgr.SY_COMM_ZHIDAO_SPECIALIST;
        }
        queryBean.setServId(servId);
        queryBean.setSelect("distinct(SPEC_ID), SPEC_SORT,USER_ID");
        if (order != null && order.length() > 0) {
            queryBean.setOrder(order);
        }
        if (showNum > 0) {
            queryBean.setQueryPageShowNum(showNum);
        }
        if (curPage > 0) {
            queryBean.setQueryPageNowPage(curPage);
        }
        OutBean outBean = ServMgr.act(queryBean);

        return outBean;
    }

    /**
     * 根据userId获取上周回答数量
     * @param paramBean - userId
     * @return - 回答数量 questionOutBean.getInt("_OKCOUNT_")
     */
    public OutBean getLastWeekAskCount(ParamBean paramBean) {
        String userId = paramBean.getStr("userId");
        ParamBean queryQuestionBean = new ParamBean(ServMgr.SY_COMM_ZHIDAO_ANSWER, ServMgr.ACT_COUNT);

        // 获取上周日期
        Date lastWeek = DateUtils.addWeeks(DateUtils.getDateFromString(DateUtils.getFirstDateOfWeek()), -1);
        Date lastSunWeek = DateUtils.addDays(lastWeek, 6);
        String lastWeekStr = DateUtils.getStringFromDate(lastWeek, DateUtils.FORMAT_TIMESTAMP);
        String lastSunWeekStr = DateUtils.getStringFromDate(lastSunWeek, DateUtils.FORMAT_TIMESTAMP);
        queryQuestionBean.setWhere("and S_USER='" + userId
                + "' and S_ATIME >'" + lastWeekStr + "' and S_ATIME < '" + lastSunWeekStr + "'");
        OutBean questionOutBean = ServMgr.act(queryQuestionBean);
        return questionOutBean;
    }

    /**
     * 获取所有关于用户关注的信息，包括被关注和提问回答数量
     * @param userQuery - 传入参数userID
     * @return - 传出参数
     */
    public OutBean getAllCount(ParamBean userQuery) {
        ZhidaoServ zhidaoServ = new ZhidaoServ();

        // 统计关注的人数
        int followCounter = zhidaoServ.getUserFollowCount(userQuery).getCount();

        // 统计被关注的人数
        int followedCounter = zhidaoServ.getUserFansCount(userQuery).getCount();

        // 统计关注的问题
        int quesFollowCounter = zhidaoServ.getQuestionFollowCount(userQuery).getCount();

        // 我的提问数量
        int askCount = zhidaoServ.getAskCount(userQuery).getCount();

        // 我的回答数量
        int answerCount = zhidaoServ.getAnswerCount(userQuery).getCount();

        //我的收藏的数量
        int favoriteCount = zhidaoServ.getFavoriteCount(userQuery).getCount();
        
        // 统计关注的分类列表
        List<Bean> categoryList = zhidaoServ.getUserFollowCategory(userQuery).getDataList();

        // 关注的分类数
        int categoryCount = zhidaoServ.getUserFollowCategory(userQuery).getCount();

        // 获取用户的总积分
        int integralCount = zhidaoServ.getUserIntegralCount(userQuery).getInt("integralCount");

        // 获取用户的赞同数
        int likeVoteCount = zhidaoServ.getLikeVoteCount(userQuery);

        // 获取用户的分享数
        int shareCount = zhidaoServ.getShareCount(userQuery);
        

        OutBean outBean = new OutBean();
        outBean.set("followCounter", followCounter);
        outBean.set("followedCounter", followedCounter);
        outBean.set("quesFollowCounter", quesFollowCounter);
        outBean.set("askCount", askCount);
        outBean.set("answerCount", answerCount);
        outBean.set("categoryList", categoryList);
        outBean.set("categoryCount", categoryCount);
        outBean.set("integralCount", integralCount);
        outBean.set("likeVoteCount", likeVoteCount);
        outBean.set("shareCount", shareCount);
        outBean.set("favoriteCount", favoriteCount);
        return outBean;
    }

    /**
     * 统计提问数量
     * @param paramBean - 参数bean
     * @return 结果bean TODO 使用 Servmgr.ACT_COUNT
     */
    public OutBean getAskCount(ParamBean paramBean) {
        String userId = paramBean.getStr("userId");
        String chnlId = paramBean.getStr("chnlId");

        StringBuilder whereBuilder = new StringBuilder();
        ParamBean askParam = new ParamBean();
        askParam.setServId(ServMgr.SY_COMM_ZHIDAO_QUESTION);
        askParam.setAct(ServMgr.ACT_QUERY);
        askParam.setQueryPageShowNum(1);

        if (userId != null && userId.length() > 0) {
            // askParam.set("S_USER", userId);
            whereBuilder.append(" AND S_USER='" + userId + "' ");
        }
        if (null != chnlId && 0 < chnlId.length()) {
            // askParam.set("CHNL_ID", chnlId);
            List<Bean> treeWhere = new ArrayList<Bean>();
            treeWhere.add(new Bean().set("DICT_ITEM", "CHNL_ID").set("DICT_VALUE", chnlId));
            askParam.set("_treeWhere", treeWhere);
        }

        if (whereBuilder.toString().length() > 0) {
            askParam.setWhere(whereBuilder.toString());
        }

        return ServMgr.act(askParam);
    }

    /**
     * 统计回答数量
     * @param paramBean - 参数bean
     * @return 结果bean
     */
    public OutBean getAnswerCount(ParamBean paramBean) {
        String userId = paramBean.getStr("userId");

        ParamBean answerParam = new ParamBean();
        answerParam.setServId(ServMgr.SY_COMM_ZHIDAO_ANSWER);
        answerParam.setAct(ServMgr.ACT_COUNT);
        if (userId != null && userId.length() > 0) {
            answerParam.set("S_USER", userId);
        }
        return ServMgr.act(answerParam);
    }

    /**
     * 获取指定问题的关注人数
     * @param paramBean - 参数bean
     * @return - outBean
     */
    public OutBean getAskFollowCount(ParamBean paramBean) {
        String questionId = paramBean.getStr("questionId");

        ParamBean followQuery = new ParamBean();
        followQuery.setServId(ServMgr.SY_COMM_ZHIDAO_QUESTION_FOLLOW);
        followQuery.setAct(ServMgr.ACT_COUNT);
        followQuery.set("DATA_ID", questionId);
        followQuery.set("SERV_ID", "SY_COMM_ZHIDAO_QUESTION_FOLLOW");
        return ServMgr.act(followQuery);
    }

    /**
     * 统计关注XX人
     * @param paramBean - 参数bean
     * @return - outbean
     */
    public OutBean getUserFollowCount(ParamBean paramBean) {
        String userId = paramBean.getStr("userId");

        ParamBean followQuery = new ParamBean();
        followQuery.setServId(ServMgr.SY_COMM_ZHIDAO_PERSON_FOLLOW);
        followQuery.setAct(ServMgr.ACT_COUNT);
        followQuery.set("USER_CODE", userId);
        //followQuery.set("SERV_ID", "SY_COMM_ZHIDAO_PERSON_FOLLOW");
        return ServMgr.act(followQuery);
    }

    /**
     * 统计关注XX问题
     * @param paramBean - 参数bean
     * @return - outbean
     */
    public OutBean getQuestionFollowCount(ParamBean paramBean) {
        String userId = paramBean.getStr("userId");

        ParamBean followQuery = new ParamBean();
        followQuery.setServId(ServMgr.SY_COMM_ZHIDAO_QUESTION_FOLLOW);
        followQuery.setAct(ServMgr.ACT_COUNT);
        followQuery.set("USER_CODE", userId);
        followQuery.set("SERV_ID", "SY_COMM_ZHIDAO_QUESTION_FOLLOW");
        return ServMgr.act(followQuery);
    }

    /**
     * 统计被关注人数
     * @param paramBean - 参数bean
     * @return - outbean
     */
    public OutBean getUserFansCount(ParamBean paramBean) {
        String userId = paramBean.getStr("userId");

        ParamBean followedQuery = new ParamBean();
        followedQuery.setServId(ServMgr.SY_COMM_ZHIDAO_PERSON_FOLLOW);
        followedQuery.setAct(ServMgr.ACT_COUNT);
        followedQuery.set("DATA_ID", userId);
        followedQuery.set("SERV_ID", "SY_COMM_ZHIDAO_PERSON_FOLLOW");
        return ServMgr.act(followedQuery);
    }

    /**
     * 获取用户的总积分
     * @param paramBean - 参数bean
     * @return - outBean
     */
    public OutBean getUserIntegralCount(ParamBean paramBean) {
        String userId = paramBean.getStr("userId");

        ParamBean integralQuery = new ParamBean();
        integralQuery.setServId(ServMgr.SY_COMM_INTEGRAL);
        integralQuery.setAct(ServMgr.ACT_FINDS);
        integralQuery.set("USER_ID", userId);
        integralQuery.set("SERV_GROUP", "SY_COMM_ZHIDAO");

        int integralCount = 0;
        List<Bean> integralList = ServMgr.act(integralQuery).getDataList();
        if (integralList.size() > 0) {
            integralCount = integralList.get(0).getInt("INTEGRAL_VALUE");
        }
        return new OutBean().set("integralCount", integralCount);
    }

    /**
     * 获取用户关注分类
     * @param paramBean - 参数bean
     * @return - outBean
     */
    public OutBean getUserFollowCategory(ParamBean paramBean) {
        String userId = paramBean.getStr("userId");
        int page = paramBean.getInt("page");
        int count = paramBean.getInt("count");

        ParamBean followCategoryQuery = new ParamBean();
        followCategoryQuery.setServId(ServMgr.SY_COMM_ZHIDAO_CATEGORY_FOLLOW);
        followCategoryQuery.setAct(ServMgr.ACT_QUERY);
        followCategoryQuery.setWhere(" and USER_CODE='" + userId + "'");
        if (count > 0) {
            followCategoryQuery.setQueryPageShowNum(count);
        } else {
            followCategoryQuery.setQueryPageShowNum(10);
        }

        if (page > 0) {
            followCategoryQuery.setQueryPageNowPage(page);
        }
        return ServMgr.act(followCategoryQuery);
    }

    /**
     * 获取知道积分排行
     * @param paramBean - 参数bean
     * @return - outbean
     */
    public OutBean topStatistics(ParamBean paramBean) {
        return new IntegralServ().zhidaoTopStatistics(paramBean);
    }

    /**
     * 获取知道积分排行(根据chnlId)
     * @param paramBean - 参数bean
     * @return - outbean
     */
    public OutBean topStatisticsByChnl(ParamBean paramBean) {
        return new IntegralServ().zhidaoTopStatisticsByChnl(paramBean);
    }

    /**
     * 获取知道用户动态
     * @param paramBean - 参数bean
     * @return - outBean
     */
    public OutBean getUserActivity(ParamBean paramBean) {
        String userId = paramBean.getStr("userId");
        int page = paramBean.getInt("page");
        page = (page == 0) ? 1 : page;
        // 摘要长度
        int summaryLength = paramBean.getInt("summaryLength");

        ParamBean queryBean = new ParamBean(ServMgr.SY_COMM_ZHIDAO_EVENT, ServMgr.ACT_QUERY);

        int count = paramBean.getInt("count");

        if (count > 0) {
            queryBean.setQueryPageShowNum(count);
        } else {
            queryBean.setQueryPageShowNum(10);
        }

        queryBean.setQueryPageNowPage(page);

        queryBean.setOrder("  S_ATIME DESC");
        String where = " AND S_USER='" + userId + "'";
        queryBean.setQueryExtWhere(where);
        OutBean outBean = ServMgr.act(queryBean);
        List<Bean> dataList = outBean.getDataList();
        for (Bean data : dataList) {
            // 回答, 赞同回答
            if (data.getStr("ACT_CODE").equals("ZHIDAO_CREATE_ANSWER")
                    || data.getStr("ACT_CODE").equals("ZHIDAO_LIKE_ANSWER")) {
                ParamBean answerQuery = new ParamBean(ServMgr.SY_COMM_ZHIDAO_ANSWER, ServMgr.ACT_BYID);
                answerQuery.setId(data.getStr("DATA_ID"));
                Bean answer = null ;
                try{
                    	answer = ServMgr.act(answerQuery);
                }catch(Exception e){
                	
                }
          	  	if(answer == null || "".equals(answer.getId())){
          	  		answer  = new Bean();
                    answer.setId(data.getStr("DATA_ID"));
                    answer.set("A_CONTENT", "该回复用户已删除");
          	  	}
          	  	String text = answer.getStr("A_CONTENT");
          	  	// 标识是否显示 阅读更多
          	  	data.set("readMore", text.length() > summaryLength);
          	  	text = Lang.getSummary(text, summaryLength);
          	  	answer.set("A_CONTENT", text);
          	  	data.set("answer", answer);
            }

            // 如果是人员的关注，要查询出这个人员的照片
            if (data.getStr("ACT_CODE").equals("ZHIDAO_FOLLOW_USER")) {
                data.set("DATA_ID_IMG", UserMgr.getUser(data.getStr("DATA_ID")).getImg());
            }
        }
        return outBean;
    }
    
    public OutBean getUserAdoptionRate(ParamBean param)
    {
      return new AnswerServ().getAdoptionRate(param);
    }

    /**
     * 根据问题Id获取此条问题的一条最新动态
     * @param param - 问题Id的集合，用,隔开
     * @return - 返回问题集合，里面有最新动态
     */
    public OutBean getNewActivity(ParamBean param) {
        String[] qIdArray = param.getStr("qIds").split(",");
        List<Bean> activityList = new ArrayList<Bean>();
        for (int i = 0; i < qIdArray.length; i++) {
            // 问题Id
            String qId = qIdArray[i];
            Bean activity = new Bean();

            // 获取EVENT事件表中的最新动态(提了一个问题/关注了问题)
            ParamBean query = new ParamBean();
            query.setQueryExtWhere(" and EVENT_GROUP = 'SY_COMM_ZHIDAO' and SERV_ID = 'SY_COMM_ZHIDAO_QUESTION' " 
                        + "and DATA_ID = '" + qId + "'");
            query.setOrder("S_ATIME DESC");
            query.setQueryPageShowNum(1);
            query.setQueryPageNowPage(1);
            OutBean eventOutBean = ServMgr.act(ServMgr.SY_COMM_EVENT, ServMgr.ACT_QUERY, query);
            Bean eventActivity = new Bean();
            if (eventOutBean.getDataList().size() > 0) {
               eventActivity = eventOutBean.getDataList().get(0); 
            } 

            // 从回答表中查询此问题的最新的回答
            ParamBean ansQuery = new ParamBean();
            ansQuery.setQueryExtWhere(" and Q_ID = '" + qId + "'");
            ansQuery.setOrder("S_ATIME DESC");
            ansQuery.setQueryPageNowPage(1);
            ansQuery.setQueryPageShowNum(1);
            OutBean ansOutBean = ServMgr.act(ServMgr.SY_COMM_ZHIDAO_ANSWER, ServMgr.ACT_QUERY, ansQuery);
            Bean ansActivity = new Bean();
            if (ansOutBean.getDataList().size() > 0) {
                ansActivity = ansOutBean.getDataList().get(0);
            }
            
            //如果有回答的最新动态
            if (ansActivity != null) {
                String ansTime = ansActivity.getStr("S_ATIME");
                String eventTime = eventActivity.getStr("S_ATIME");
                //判断两者的时间，确定哪条信息将作为最新动态
                if (ansTime.compareTo(eventTime) > 0) {
                    activity.set("S_ATIME", ansActivity.getStr("S_ATIME"));
                    activity.set("USER_NAME", ansActivity.getStr("S_USER__NAME"));
                    activity.set("ACT_CODE_STR", "回答了");
                } else {
                    activity.set("S_ATIME", eventActivity.getStr("S_ATIME"));
                    activity.set("USER_NAME", eventActivity.getStr("USER_CODE__NAME"));
                    if (eventActivity.getStr("ACT_CODE").equals("ZHIDAO_FOLLOW_ASK")) {
                        activity.set("ACT_CODE_STR", "关注了");
                    } else {
                        activity.set("ACT_CODE_STR", "提出了");
                    }
                }
            } else {
                activity.set("S_ATIME", eventActivity.getStr("S_ATIME"));
                activity.set("USER_NAME", eventActivity.getStr("USER_CODE__NAME"));
                if (eventActivity.getStr("ACT_CODE").equals("ZHIDAO_FOLLOW_ASK")) {
                    activity.set("ACT_CODE_STR", "关注了");
                } else {
                    activity.set("ACT_CODE_STR", "提出了");
                }
            }
            activityList.add(i, activity);
        }
        return new OutBean().setData(activityList);
    }

    /**
     * 获取对回答投票用户
     * @param answerId - 回答ID
     * @param voteType - 投票类型? 1:支持,2:反对
     * @return -结果bean
     */
    private OutBean getAnswerVoteUser(String answerId, int voteType) {
        ParamBean answQuery = new ParamBean(ServMgr.SY_COMM_ZHIDAO_ANSWER_VOTE, ServMgr.ACT_QUERY);
        // answQuery.setSelect("A_ANONY");
        StringBuffer searchWhere = new StringBuffer();
        searchWhere.append(" and SERV_ID = 'SY_COMM_ZHIDAO_ANSWER'");
        searchWhere.append(" and DATA_ID='" + answerId + "'");
        searchWhere.append(" and VOTE_VALUE = " + voteType);
        answQuery.setQuerySearchWhere(searchWhere.toString());
        return ServMgr.act(answQuery);
    }

    /**
     * 获取我关注的用户信息 用于 知道-精彩推荐
     * @param paramBean - 参数bean
     * @return - outBean
     */
    public OutBean getMyFollowUserInfo(ParamBean paramBean) {
        UserBean currentUser = Context.getUserBean();
        String userId = currentUser.getCode();

        int page = paramBean.getInt("page");
        int count = paramBean.getInt("count");
        String pageName = paramBean.getStr("pageName");

        ParamBean queryBean = new ParamBean();
        queryBean.setServId(ServMgr.SY_COMM_ZHIDAO_PERSON_FOLLOW);
        queryBean.setAct(ServMgr.ACT_QUERY);
        queryBean.setQueryExtWhere(" and USER_CODE='" + userId + "'");

        if ("myfollow".equals(pageName)) {
            queryBean.setQueryNoPageFlag(true);
        } else {
            if (count > 0) {
                queryBean.setQueryPageShowNum(count);
            } else {
                queryBean.setQueryPageShowNum(10);
            }
            if (page > 0) {
                queryBean.setQueryPageNowPage(page);
            }
        }

        OutBean outBean = ServMgr.act(queryBean);
        return outBean;
    }

    /**
     * 获取我关注的人 用于 知道-我的关注
     * @param paramBean - 参数bean
     * @return - outBean
     */
    public OutBean getMyFollowPerson(ParamBean paramBean) {
        UserBean currentUser = Context.getUserBean();
        String userId = currentUser.getCode();

        int page = paramBean.getInt("page");
        int count = paramBean.getInt("count");

        ParamBean queryBean = new ParamBean();
        queryBean.setServId(ServMgr.SY_COMM_ZHIDAO_PERSON_FOLLOW);
        queryBean.setAct(ServMgr.ACT_QUERY);
        queryBean.setQueryExtWhere(" and USER_CODE='" + userId + "'");
        if (count > 0) {
            queryBean.setQueryPageShowNum(count);
        } else {
            queryBean.setQueryPageShowNum(10);
        }

        if (page > 0) {
            queryBean.setQueryPageNowPage(page);
        }

        OutBean outBean = ServMgr.act(queryBean);
        List<Bean> dataList = outBean.getDataList();
        ParamBean p1 = new ParamBean();
        ParamBean p2 = new ParamBean(ServMgr.SY_COMM_ZHIDAO_EVENT, ServMgr.ACT_FINDS);
        p2.setOrder("S_MTIME DESC");
        for (Bean data : dataList) {
            String userCode = data.getStr("DATA_ID");
            UserBean user = UserMgr.getUser(userCode);
            data.set("user", user);
            // 获取专家领域
            p1.set("userId", userCode);
            OutBean tempBean = getSpecialistSubject(p1);
            List<Bean> list = tempBean.getDataList();
            data.set("specialist", list);

            // 获得最近问答时间
            p2.setWhere(" AND S_USER='" + userCode + "'");
            List<Bean> activityList = ServMgr.act(p2).getDataList();
            for (Bean b : activityList) {
                if (b.getStr("ACT_CODE").equals("ZHIDAO_CREATE_ASK")
                        || b.getStr("ACT_CODE").equals("ZHIDAO_CREATE_ANSWER")) {
                    // 如果 回答 和赞同，则取 Q_ID 和 Q_TITLE
                    data.set("latestTime", b.get("S_ATIME"));
                    break;
                }
            }
        }
        return outBean;
    }

    /**
     * 获取我关注的问题
     * @param paramBean - 参数bean
     * @return - outBean
     */
    public OutBean getMyFollowQuestion(ParamBean paramBean) {
        String userId = paramBean.getStr("userId");
        if ("".equals(userId) || userId == null) {
            UserBean currentUser = Context.getUserBean();
            userId = currentUser.getCode();
        }

        int page = paramBean.getInt("page");
        int count = paramBean.getInt("count");

        ParamBean queryBean = new ParamBean();
        queryBean.setServId(ServMgr.SY_COMM_ZHIDAO_QUESTION_FOLLOW);
        queryBean.setAct(ServMgr.ACT_QUERY);
        queryBean.setOrder("S_MTIME DESC");
        queryBean.setQueryExtWhere(" and USER_CODE='" + userId + "'");
        if (count > 0) {
            queryBean.setQueryPageShowNum(count);
        } else {
            queryBean.setQueryPageShowNum(10);
        }

        if (page > 0) {
            queryBean.setQueryPageNowPage(page);
        } else {
            queryBean.setQueryPageNowPage(1);
        }

        OutBean outBean = ServMgr.act(queryBean);
        List<Bean> dataList = outBean.getDataList();
        ParamBean tempParam = new ParamBean();
        tempParam.set("SERV_ID", ServMgr.SY_COMM_ZHIDAO_ANSWER);
        for (Bean data : dataList) {
            String dataId = data.getStr("DATA_ID");
            tempParam.set("DATA_ID", dataId);
            int sum = getVoteCount(tempParam);
            data.set("VOTE_VALUE", sum);
        }
        return outBean;
    }

    /**
     * 获取我关注的分类
     * @param paramBean - 参数bean
     * @return - outBean
     */
    public OutBean getMyFollowCategory(ParamBean paramBean) {
        int page = paramBean.getInt("page");
        page = page == 0 ? 1 : page;
        int count = paramBean.getInt("count");
        count = count == 0 ? 20 : count;

        ParamBean queryBean = new ParamBean();
        queryBean.setServId(ServMgr.SY_COMM_ZHIDAO_MY_CATEGORY_FOLLOW);
        queryBean.setAct(ServMgr.ACT_QUERY);
        queryBean.setQueryPageShowNum(count);
        queryBean.setQueryPageNowPage(page);

        OutBean outBean = ServMgr.act(queryBean);
        List<Bean> dataList = outBean.getDataList();
        // 根据分类获取该分类下的最新问题（TODO 以后取最新问答）
        ParamBean tempParam = new ParamBean(ServMgr.SY_COMM_ZHIDAO_QUESTION, ServMgr.ACT_QUERY);
        tempParam.setOrder("S_MTIME DESC");

        List<Bean> treeWhere = new ArrayList<Bean>();
        Bean bean = new Bean();
        bean.set("DICT_ITEM", "CHNL_ID");
        for (Bean data : dataList) {
            String dataId = data.getStr("DATA_ID");
            bean.set("DICT_VALUE", dataId);
            treeWhere.add(bean);

            tempParam.set("_treeWhere", treeWhere);

            OutBean tempBean = ServMgr.act(tempParam);

            List<Bean> list = tempBean.getDataList();
            if (list.size() > 0) {
                Bean b = list.get(0);
                data.set("Q_ID", b.get("Q_ID"));
                data.set("Q_TITLE", b.get("Q_TITLE"));
                // TODO 暂取问题提问时间，以后判断如果该问题下有回答，取最新回答时间；如果没有回答，则取问题提问时间
                data.set("QA_MTIME", b.get("S_MTIME"));
            }
            treeWhere.clear();
        }
        return outBean;
    }

    /**
     * 获取投票数
     * @param param -传入参数 userid
     * @return -投票数
     */
    public int getVoteCount(ParamBean param) {
        // 问题ID
        String dataId = param.getStr("DATA_ID");
        String servId = param.getStr("SERV_ID");

        int count = 0;

        ParamBean pm = new ParamBean(ServMgr.SY_COMM_VOTE, ServMgr.ACT_FINDS);
        // 投票数
        pm.setSelect("SUM(VOTE_VALUE) as VOTE_VALUE");
        pm.set("DATA_ID", dataId);
        pm.set("SERV_ID", servId);

        OutBean outBean = ServMgr.act(pm);
        List<Bean> list = outBean.getDataList();
        if (list.size() > 0) {
            count = Integer.parseInt(list.get(0).getStr("VOTE_VALUE"));
        }

        return count;
    }

    /**
     * 增加人员关注
     * @param param - 传入参数，只需要DATA_ID
     * @return - 增加是否成功
     */
    public OutBean addPersonFollow(ParamBean param) {
        String dataId = param.getStr("DATA_ID");
        // 查询被关注人的信息
        UserBean userBean = UserMgr.getUser(dataId);
        // 封装数据信息
        ParamBean newParam = new ParamBean();
        newParam.set("SERV_ID", "SY_COMM_ZHIDAO_PERSON_FOLLOW");
        newParam.set("DATA_ID", dataId);
        newParam.set("DATA_DIS_NAME", userBean.getName());
        newParam.set("DATA_OWNER", userBean.getCode());
        // 调用FollowServ的增加方法
        FollowServ followServ = new FollowServ();
        OutBean outBean = followServ.addFollow(newParam);

        // 保存事件
        String act = "ZHIDAO_FOLLOW_USER";
        EventMgr.save(ZhidaoServ.SY_COMM_ZHIDAO, ServMgr.SY_ORG_USER, dataId, act,
                userBean.getName(), userBean.getCode());

        return outBean;
    }

    /**
     * 取消人员关注
     * @param param - 传入参数，只需要DATA_ID
     * @return - 取消是否成功
     */
    public OutBean deletePersonFollow(ParamBean param) {
        String dataId = param.getStr("DATA_ID");
        // 封装数据信息
        ParamBean newParam = new ParamBean();
        newParam.set("SERV_ID", "SY_COMM_ZHIDAO_PERSON_FOLLOW");
        newParam.set("DATA_ID", dataId);
        // 调用FollowServ的增加方法
        FollowServ followServ = new FollowServ();
        OutBean outBean = followServ.deleteFollow(newParam);
        return outBean;
    }

    /**
     * 增加分类关注
     * @param param - 传入参数，只需要DATA_ID
     * @return - 增加是否成功
     */
    public OutBean addCategoryFollow(ParamBean param) {
        String dataId = param.getStr("DATA_ID");
        // 查询被关注分类的信息
        ParamBean categoryQuery = new ParamBean();
        categoryQuery.setId(dataId);
        categoryQuery.setServId("SY_COMM_ZHIDAO_CHNL");
        categoryQuery.setAct(ServMgr.ACT_BYID);
        OutBean categoryBean = ServMgr.act(categoryQuery);
        // 封装数据信息
        ParamBean newParam = new ParamBean();
        newParam.set("SERV_ID", "SY_COMM_ZHIDAO_CATEGORY_FOLLOW");
        newParam.set("DATA_ID", dataId);
        newParam.set("DATA_DIS_NAME", categoryBean.getStr("CHNL_NAME"));
        newParam.set("DATA_OWNER", categoryBean.getStr("S_USER"));
        // 调用FollowServ的增加方法
        FollowServ followServ = new FollowServ();
        OutBean outBean = followServ.addFollow(newParam);

        // 保存事件
        String act = "ZHIDAO_FOLLOW_CATEGORY";
        EventMgr.save(ZhidaoServ.SY_COMM_ZHIDAO, ServMgr.SY_COMM_ZHIDAO_CHNL, dataId, act,
                categoryBean.getStr("CHNL_NAME"), categoryBean.getStr("S_USER"));

        return outBean;
    }

    /**
     * 批量添加分类关注
     * @param param - 传入参数 - 一个使用","分割的dataID字符串
     */
    public void addBatchCategoryFollow(ParamBean param) {
        String dataIds = param.getStr("DATA_IDS");
        String[] dataId = dataIds.split(",");
        for (String string : dataId) {
            ParamBean query = new ParamBean();
            query.set("DATA_ID", string);
            addCategoryFollow(query);
        }
    }

    /**
     * 取消分类关注
     * @param param - 传入参数，只需要DATA_ID
     * @return - 取消是否成功
     */
    public OutBean deleteCategoryFollow(ParamBean param) {
        String dataId = param.getStr("DATA_ID");
        // 封装数据信息
        ParamBean newParam = new ParamBean();
        newParam.set("SERV_ID", "SY_COMM_ZHIDAO_CATEGORY_FOLLOW");
        newParam.set("DATA_ID", dataId);
        // 调用FollowServ的增加方法
        FollowServ followServ = new FollowServ();
        OutBean outBean = followServ.deleteFollow(newParam);
        return outBean;
    }

    /**
     * 批量删除分类关注
     * @param param - 传入参数 - 一个使用","分割的dataID字符串
     */
    public void deleteBatchCategoryFollow(ParamBean param) {
        String dataIds = param.getStr("DATA_IDS");
        String[] dataId = dataIds.split(",");
        for (String string : dataId) {
            ParamBean query = new ParamBean();
            query.set("DATA_ID", string);
            deleteCategoryFollow(query);
        }
    }

    /**
     * 增加问题关注
     * @param param - 传入参数，只需要DATA_ID
     * @return - 增加是否成功
     */
    public OutBean addQuestionFollow(ParamBean param) {
        String dataId = param.getStr("DATA_ID");
        // 查询被关注问题的信息
        ParamBean questionQuery = new ParamBean();
        questionQuery.setId(dataId);
        questionQuery.setServId("SY_COMM_ZHIDAO_QUESTION");
        questionQuery.setAct(ServMgr.ACT_BYID);
        OutBean questionBean = ServMgr.act(questionQuery);
        // 封装数据信息
        ParamBean newParam = new ParamBean();
        newParam.set("SERV_ID", "SY_COMM_ZHIDAO_QUESTION_FOLLOW");
        newParam.set("DATA_ID", dataId);
        newParam.set("DATA_DIS_NAME", questionBean.getStr("Q_TITLE"));
        newParam.set("DATA_OWNER", questionBean.getStr("S_USER"));
        // 调用FollowServ的增加方法
        FollowServ followServ = new FollowServ();
        OutBean outBean = followServ.addFollow(newParam);

        // 保存事件
        String act = "ZHIDAO_FOLLOW_ASK";
        EventMgr.save(ZhidaoServ.SY_COMM_ZHIDAO, ServMgr.SY_COMM_ZHIDAO_QUESTION, dataId, act,
                questionBean.getStr("Q_TITLE"), questionBean.getStr("S_USER"));

        return outBean;
    }

    /**
     * 取消问题关注
     * @param param - 传入参数，需要DATA_ID
     * @return - 取消是否成功
     */
    public OutBean deleteQuestionFollow(ParamBean param) {
        String dataId = param.getStr("DATA_ID");
        // 封装数据信息
        ParamBean newParam = new ParamBean();
        newParam.set("SERV_ID", "SY_COMM_ZHIDAO_QUESTION_FOLLOW");
        newParam.set("DATA_ID", dataId);
        // 调用FollowServ的增加方法
        FollowServ followServ = new FollowServ();
        OutBean outBean = followServ.deleteFollow(newParam);
        return outBean;
    }

    /**
     * 根据userId来查找这个人提出的最新的问题和投票数最多的回答
     * @param param - 传入参数userId
     * @return - 传出参数，问题和回答
     */
    public OutBean getNewQuestionAnswerByPerson(ParamBean param) {
        String dataId = param.getStr("DATA_ID");
        int summaryLength = param.getInt("summaryLength");
        OutBean outBean = new OutBean();

        // 获取用户提出的最新的问题
        // ParamBean questionParam = new ParamBean();
        // questionParam.setAct(ServMgr.ACT_QUERY);
        // questionParam.setServId(ServMgr.SY_COMM_ZHIDAO_QUESTION);
        // questionParam.setQueryPageNowPage(1);
        // questionParam.setQueryPageShowNum(1);
        // questionParam.setQueryPageOrder("S_ATIME DESC");
        // questionParam.setQueryExtWhere(" and S_USER = '" + dataId + "'");
        // OutBean questionBean = ServMgr.act(questionParam);

        // 拿出最佳答案
        // if (questionBean.getDataList().size() > 0) {
        // String answerId = questionBean.getDataList().get(0).getStr("ANS_ID");
        //
        // ParamBean answerParam = new ParamBean();
        // answerParam.setServId(ServMgr.SY_COMM_ZHIDAO_ANSWER);
        // if (answerId != null && answerId.length() > 0) {
        // answerParam.setAct(ServMgr.ACT_BYID);
        // answerParam.setId(answerId);
        // } else {
        // answerParam.setAct(ServMgr.ACT_QUERY);
        // String questionId = questionBean.getDataList().get(0).getStr("Q_ID");
        // answerParam.setQueryPageNowPage(1);
        // answerParam.setQueryPageShowNum(1);
        // answerParam.setOrder("A_LIKE_VOTE DESC");
        // answerParam.setQueryExtWhere(" and Q_ID = '" + questionId + "'");
        // }
        //
        // OutBean answerBean = ServMgr.act(answerParam);
        //
        // if (answerBean.getDataList().size() > 0) {
        // if (0 < summaryLength) {
        // String text = answerBean.getDataList().get(0).getStr("A_CONTENT");
        // text = Lang.getSummary(text, summaryLength);
        // answerBean.getDataList().get(0).set("A_CONTENT", text);
        // }
        // outBean.set("answer_content", answerBean.getDataList().get(0).get("A_CONTENT"));
        // outBean.set("ANSWER", answerBean);
        // }
        // outBean.set("QUESTION", questionBean);
        // outBean.set("question_title", questionBean.getDataList().get(0).get("Q_TITLE"));
        // outBean.set("question_id", questionBean.getDataList().get(0).getStr("Q_ID"));
        // }

        // 后加的需求，根据用户的ID获取到用户最新回答的问题，并查询出这条问题的标题和提问内容
        // ParamBean myAnswerParam = new ParamBean();
        // myAnswerParam.setServId(ServMgr.SY_COMM_ZHIDAO_ANSWER);
        // myAnswerParam.setAct(ServMgr.ACT_QUERY);
        // myAnswerParam.setQueryExtWhere(" and S_USER='" + dataId + "'");
        // myAnswerParam.setQueryPageShowNum(1);
        // myAnswerParam.setQueryPageNowPage(1);
        // myAnswerParam.setOrder("S_MTIME desc");
        // OutBean myAnswerBean = ServMgr.act(myAnswerParam);
        //
        // if (myAnswerBean.getDataList().size() > 0) {
        // outBean.set("MY_ANSWER", myAnswerBean);
        // outBean.set("my_answer_q_title", myAnswerBean.getDataList().get(0).get("Q_TITLE"));
        // outBean.set("my_answer_q_id", myAnswerBean.getDataList().get(0).get("Q_ID"));
        // if (0 < summaryLength) {
        // String text = myAnswerBean.getDataList().get(0).getStr("A_CONTENT");
        // text = Lang.getSummary(text, summaryLength);
        // myAnswerBean.getDataList().get(0).set("A_CONTENT", text);
        // }
        // outBean.set("my_answer_content", myAnswerBean.getDataList().get(0).get("A_CONTENT"));
        // }

        // 查询出这个人的信息
        UserBean userBean = UserMgr.getUser(dataId);
        int userStatus = Context.userOnline(userBean.getCode());
        userBean.set("USER_STATUS", userStatus);
        outBean.set("USER", userBean);

        // 查询专家信息
        List<Bean> specList = getSpecialist(new ParamBean().set("userId", dataId)).getDataList();
        if (specList.size() > 0) {
            Bean spec = specList.get(0);

            // 获取专家的类型
            String specType = spec.getStr("SPEC_TYPE");
            outBean.set("SPEC_TYPE", specType);
            // 将专家简介截串儿
            String specStr = spec.getStr("SPEC_DESC");
            if ("".equals(specStr)) {
                outBean.set("SPEC", "此专家简介为空！");
            } else {
                if (specStr.length() > 54) {
                    specStr = Lang.getSummary(specStr, 54) + "...";
                }
                outBean.set("SPEC", specStr);
            }
        } else {
            outBean.set("SPEC", "");
        }

        // 查询这个人的专家领域
        List<Bean> subList = getSpecialistSubject(new ParamBean().set("userId", dataId)).getDataList();
        String subStr = "";
        if (subList.size() > 0) {
            StringBuffer subStrBuf = new StringBuffer();
            for (Bean bean : subList) {
                subStrBuf.append(bean.getStr("CHNL_NAME"));
                subStrBuf.append("/");
            }
            subStr = subStrBuf.substring(0,
                    subStrBuf.length() - 1);
        }
        outBean.set("SUB", subStr);

        // 查询出这个人的最新动态5条记录
        List<Bean> activityList = getUserActivity(
                new ParamBean().set("userId", dataId).set("summaryLength", summaryLength).set("count", 5))
                .getDataList();
        outBean.set("activityList", activityList);

        return outBean;
    }

    /**
     * 统计提出问题的个数和回有回答的个数
     * @param param - 参数
     * @return - 返回结果
     */
    /*
     * public OutBean getTotalSolveQuestion(ParamBean param) { // 查询问题个数 ParamBean askQuery = new ParamBean();
     * askQuery.setServId(ServMgr.SY_COMM_ZHIDAO_QUESTION); askQuery.setAct(ServMgr.ACT_COUNT); //
     * query.setWhere(" and Q_ANSWER_COUNTER > 0"); // query.setQueryExtWhere("Q_ANSWER_COUNTER > 0"); int askCounter =
     * ServMgr.act(askQuery).getCount();
     * 
     * // 查询回答个数 ParamBean answerQuery = new ParamBean(); answerQuery.setServId(ServMgr.SY_COMM_ZHIDAO_ANSWER);
     * answerQuery.setAct(ServMgr.ACT_COUNT); int answerCounter = ServMgr.act(answerQuery).getCount();
     * 
     * return new OutBean().set("askCounter", askCounter).set("answerCounter", answerCounter); }
     */

    /**
     * 查询出三个不同领域的专家的信息和最新回答的问题
     * @param param - 传入参数
     * @return - 返回参数
     */
    public OutBean getThreeSpec(ParamBean param) {
        OutBean outBean = new OutBean();

        // 领导
        ParamBean leaderQuery = new ParamBean();
        leaderQuery.setServId(ServMgr.SY_COMM_ZHIDAO_SPECIALIST);
        leaderQuery.setAct(ServMgr.ACT_QUERY);
        leaderQuery.setQueryExtWhere(" and SPEC_TYPE = 1");
        OutBean leaderBean = ServMgr.act(leaderQuery);
        List<Bean> leaderList = leaderBean.getDataList();
        ParamBean leaderAnswerQuery = new ParamBean();

        if (leaderList.size() > 0) {
            StringBuffer leaderWhere = new StringBuffer();
            leaderWhere.append("(");
            for (Bean bean : leaderList) {
                leaderWhere.append("'");
                leaderWhere.append(bean.getStr("USER_ID"));
                leaderWhere.append("'");
                leaderWhere.append(",");
            }
            String leaderWhereStr = leaderWhere.substring(0, leaderWhere.length() - 1) + ")";
            leaderAnswerQuery.setQueryExtWhere(" and S_USER in " + leaderWhereStr);
            leaderAnswerQuery.setServId(ServMgr.SY_COMM_ZHIDAO_ANSWER);
            leaderAnswerQuery.setAct(ServMgr.ACT_QUERY);
            leaderAnswerQuery.setOrder(" S_ATIME desc");
            OutBean leaderAnswer = ServMgr.act(leaderAnswerQuery);
            Bean leaderResult = null;
            if (leaderAnswer.getDataList().size() > 0) {
                leaderResult = leaderAnswer.getDataList().get(0);

                String userCode = leaderResult.getStr("S_USER");
                for (Bean bean2 : leaderList) {
                    if (userCode.equals(bean2.getStr("USER_ID"))) {
                        leaderResult.set("leader_spec", bean2);
                    }
                }
                String text = leaderResult.getStr("A_CONTENT");
                text = Lang.getSummary(text, 30);
                leaderResult.set("A_CONTENT", text);

                String leaderTitle = leaderResult.getStr("Q_TITLE");
                leaderTitle = Lang.getSummary(leaderTitle, 13);
                leaderResult.set("Q_TITLE", leaderTitle);

                // 将职务分开，如："技术部/项目中心"
                String[] leaderdept = leaderResult.getStr("S_DEPT__NAME").split("/");
                if (leaderdept.length == 2) {
                    leaderResult.set("S_DEPT_NAME_1", leaderdept[0]);
                    leaderResult.set("S_DEPT_NAME_2", leaderdept[1]);
                } else {
                    leaderResult.set("S_DEPT_NAME_1", "无效的部门名称");
                    leaderResult.set("S_DEPT_NAME_2", "无效的部门名称");
                }
                leaderResult.set("USER_IMG", leaderResult.getStr("S_USER__IMG"));
            }
            outBean.set("leader", leaderResult);
        }

        // 职能部门
        ParamBean deptQuery = new ParamBean();
        deptQuery.setServId(ServMgr.SY_COMM_ZHIDAO_SPECIALIST);
        deptQuery.setAct(ServMgr.ACT_QUERY);
        deptQuery.setQueryExtWhere(" and SPEC_TYPE = 2");
        OutBean deptBean = ServMgr.act(deptQuery);
        List<Bean> deptList = deptBean.getDataList();

        if (deptList.size() > 0) {
            StringBuffer deptWhere = new StringBuffer();
            deptWhere.append("(");
            for (Bean bean : deptList) {
                deptWhere.append("'");
                deptWhere.append(bean.getStr("USER_ID"));
                deptWhere.append("'");
                deptWhere.append(",");
            }
            String deptWhereStr = deptWhere.substring(0, deptWhere.length() - 1) + ")";
            ParamBean deptAnswerQuery = new ParamBean();
            deptAnswerQuery.setServId(ServMgr.SY_COMM_ZHIDAO_ANSWER);
            deptAnswerQuery.setAct(ServMgr.ACT_QUERY);
            deptAnswerQuery.setOrder(" S_ATIME desc");
            deptAnswerQuery.setQueryExtWhere(" and S_USER in " + deptWhereStr);
            OutBean deptAnswer = ServMgr.act(deptAnswerQuery);
            Bean deptResult = null;
            if (deptAnswer.getDataList().size() > 0) {
                deptResult = deptAnswer.getDataList().get(0);
                String userCode = deptResult.getStr("S_USER");
                for (Bean bean2 : deptList) {
                    if (userCode.equals(bean2.getStr("USER_ID"))) {
                        deptResult.set("dept_spec", bean2);
                    }
                }
                String text = deptResult.getStr("A_CONTENT");
                text = Lang.getSummary(text, 30);
                deptResult.set("A_CONTENT", text);

                String deptTitle = deptResult.getStr("Q_TITLE");
                deptTitle = Lang.getSummary(deptTitle, 13);
                deptResult.set("Q_TITLE", deptTitle);

                // 将职务分开，如："技术部/项目中心"
                String[] deptdept = deptResult.getStr("S_DEPT__NAME").split("/");
                deptResult.set("S_DEPT_NAME_1", deptdept[0]);
                deptResult.set("S_DEPT_NAME_2", deptdept[1]);
                deptResult.set("USER_IMG", deptResult.getStr("S_USER__IMG"));
            }
            outBean.set("dept", deptResult);
        }

        // 业务专家
        ParamBean busiQuery = new ParamBean();
        busiQuery.setServId(ServMgr.SY_COMM_ZHIDAO_SPECIALIST);
        busiQuery.setAct(ServMgr.ACT_QUERY);
        busiQuery.setQueryExtWhere(" and SPEC_TYPE = 3");
        OutBean busiBean = ServMgr.act(busiQuery);
        List<Bean> busiList = busiBean.getDataList();
        ParamBean busiAnswerQuery = new ParamBean();

        if (busiList.size() > 0) {
            StringBuffer busiWhere = new StringBuffer();
            busiWhere.append("(");
            for (Bean bean : busiList) {
                busiWhere.append("'");
                busiWhere.append(bean.getStr("USER_ID"));
                busiWhere.append("'");
                busiWhere.append(",");
            }
            String busiWhereStr = busiWhere.substring(0, busiWhere.length() - 1) + ")";
            busiAnswerQuery.setQueryExtWhere(" and S_USER in " + busiWhereStr);
            busiAnswerQuery.setServId(ServMgr.SY_COMM_ZHIDAO_ANSWER);
            busiAnswerQuery.setAct(ServMgr.ACT_QUERY);
            busiAnswerQuery.setOrder(" S_ATIME desc");
            OutBean busiAnswer = ServMgr.act(busiAnswerQuery);
            Bean busiResult = null;
            if (busiAnswer.getDataList().size() > 0) {
                busiResult = busiAnswer.getDataList().get(0);
                String userCode = busiResult.getStr("S_USER");
                for (Bean bean2 : busiList) {
                    if (userCode.equals(bean2.getStr("USER_ID"))) {
                        busiResult.set("busi_spec", bean2);
                    }
                }
                String text = busiResult.getStr("A_CONTENT");
                text = Lang.getSummary(text, 30);
                busiResult.set("A_CONTENT", text);

                String busiTitle = busiResult.getStr("Q_TITLE");
                busiTitle = Lang.getSummary(busiTitle, 13);
                busiResult.set("Q_TITLE", busiTitle);

                // 将职务分开，如："技术部/项目中心"
                String[] busidept = busiResult.getStr("S_DEPT__NAME").split("/");
                busiResult.set("S_DEPT_NAME_1", busidept[0]);
                busiResult.set("S_DEPT_NAME_2", busidept[1]);
                busiResult.set("USER_IMG", busiResult.getStr("S_USER__IMG"));
            }
            outBean.set("busi", busiResult);
        }

        return outBean;
    }

    /**
     * 统计"赞同数/回答被采纳数/被分享数"
     * @param paramBean - 传入参数userId
     * @return - 返回参数
     */
    public OutBean countAnswer(ParamBean paramBean) {
        String userId = paramBean.getStr("userId");
        // 获得支持数量
        Bean likeVoteQuery = new Bean();
        likeVoteQuery.set("S_USER", userId);
        likeVoteQuery.set(Constant.PARAM_SELECT, "sum (A_LIKE_VOTE)");
        Bean likeBean = ServDao.find(ServMgr.SY_COMM_ZHIDAO_ANSWER, likeVoteQuery);
        int aLikeVoteCounter = likeBean.getInt("SUM(A_LIKE_VOTE)");
        // 获取被采纳数量
        Bean bestQuery = new Bean();
        bestQuery.set("S_USER", userId);
        bestQuery.set("A_BEST", 1);
        int aBestCounter = ServDao.count(ServMgr.SY_COMM_ZHIDAO_ANSWER, bestQuery);

        // 获取被管理员采纳数量
        Bean adminBestQuery = new Bean();
        adminBestQuery.set("S_USER", userId);
        adminBestQuery.set("A_ADMIN_BEST", 1);
        int aAdminBestCounter = ServDao.count(ServMgr.SY_COMM_ZHIDAO_ANSWER, adminBestQuery);
        
        // 获取被分享数
        ParamBean shareQuery = new ParamBean();
        shareQuery.set("userId", userId);
        int shareCounter = new ZhidaoShareServ().getShareCount(shareQuery);

        OutBean outBean = new OutBean();
        outBean.set("aLikeVoteCounter", aLikeVoteCounter);
        outBean.set("aBestCounter", aBestCounter);
        outBean.set("aAdminBestCounter", aAdminBestCounter);
        outBean.set("shareCounter", shareCounter);
        return outBean;
    }

    /**
     * 获取某人总的赞同数
     * @param paramBean - userId
     * @return 赞同数量
     */
    public int getLikeVoteCount(ParamBean paramBean) {
        String userId = paramBean.getStr("userId");
        Bean likeVoteQuery = new Bean();
        likeVoteQuery.set("S_USER", userId);
        likeVoteQuery.set(Constant.PARAM_SELECT, "sum (A_LIKE_VOTE)");
        Bean likeBean = ServDao.find(ServMgr.SY_COMM_ZHIDAO_ANSWER, likeVoteQuery);
        int aLikeVoteCounter = likeBean.getInt("SUM(A_LIKE_VOTE)");
        return aLikeVoteCounter;
    }

    /**
     * 获取某人总的分享数
     * @param paramBean - userId
     * @return 分享数量
     */
    public int getShareCount(ParamBean paramBean) {
        log.debug("获取某人总的分享数:开始");
        String userId = paramBean.getStr("userId");
        Bean shareQuery = new Bean();
        shareQuery.set("S_USER", userId);
        shareQuery.set(Constant.PARAM_SELECT, "count (SHARE_ID)");
        shareQuery.set("SERV_GROUP", "SY_COMM_ZHIDAO");
        Bean shareBean = ServDao.find(ServMgr.SY_COMM_ZHIDAO_SHARE, shareQuery);
        int shareCount = shareBean.getInt("COUNT(SHARE_ID)");
        log.debug("获取某人总的分享数:结束");
        return shareCount;
    }

    /**
     * 统计某个问题的总分享数
     * @param param - qId
     * @return 总分享数
     */
    public int getQuestionShareCount(ParamBean param) {
        log.debug("获取某个问题的总分享数：开始");
        String qId = param.getStr("qId");
        Bean query = new Bean();
        query.set("DATA_ID", qId);
        query.set(Constant.PARAM_SELECT, "count (SHARE_ID)");
        query.set("SERV_GROUP", "SY_COMM_ZHIDAO");
        query.set("SERV_ID", "SY_COMM_ZHIDAO_QUESTION");
        int count = ServDao.find(ServMgr.SY_COMM_ZHIDAO_SHARE, query).getInt("COUNT(SHARE_ID)");
        log.debug("获取某个问题的总分享数：结束");
        return count;
    }

    /**
     * 个人主页浏览量+1
     * @param param - 传入参数
     * @return - 返回值
     */
    public Bean increasePersonalIndexCounter(Bean param) {
        UserBean userBean = Context.getUserBean();
        String act = "read";
        String dataId = param.getStr("userId");
        String dataDisName = userBean.getName() + "浏览了" + dataId + "的主页";
        String dataOwner = userBean.getCode();
        ReqhisMgr.save("SY_COMM_ZHIDAO", dataId, act, dataDisName, dataOwner);
        OutBean outBean = new OutBean(param);
        outBean.setOk();
        return outBean;
    }

    /**
     * 获取个人主页浏览人数/浏览次数/最后浏览时间
     * @param paramBean - 传入参数 - 当前被阅读的主页userId
     * @return - 返回值
     */
    public OutBean getReqCounter(ParamBean paramBean) {
        OutBean outBean = new OutBean();
        String servId = "SY_COMM_ZHIDAO";
        String dataId = paramBean.getStr("userId");
        String action = "read";
        int countUser = ReqhisMgr.countUser(servId, dataId, action);
        int countReq = ReqhisMgr.countReq(servId, dataId, action);
        Bean bean = ReqhisMgr.newReq(servId, dataId, action);
        if (bean == null) {
            outBean.set("time", "暂无");
        } else {
            String timeStr = bean.getStr("S_MTIME");
            outBean.set("time", timeStr.substring(0, 16));
        }
        outBean.set("countUser", countUser);
        outBean.set("countReq", countReq);
        return outBean;
    }

    /**
     * 获取某人"关注了"或者"关注者"的列表
     * @param paramBean - 传入参数
     * @return - 返回值
     */
    public OutBean getFollowList(ParamBean paramBean) {
        int count = paramBean.getInt("count");
        String userId = paramBean.getStr("userId");
        int page = paramBean.getInt("page");
        // 是否查询的是关注的人
        String isFollow = paramBean.getStr("isFollow");

        // 获取当前的userId所关注的人的列表
        ParamBean followQuery = new ParamBean();
        followQuery.setServId(ServMgr.SY_COMM_ZHIDAO_PERSON_FOLLOW);
        if (page == 0) {
            page = 1;
        }
        followQuery.setQueryPageNowPage(page);
        followQuery.setQueryPageShowNum(count);
        String columnName = "";
        // 查询我关注的人列表
        if ("isFollow".equals(isFollow)) {
            followQuery.setQueryExtWhere(" and SERV_ID = 'SY_COMM_ZHIDAO_PERSON_FOLLOW' and USER_CODE = '" + userId
                    + "'");
            columnName = "DATA_ID";
        } else { // 查询关注了我的人的列表
            followQuery
                    .setQueryExtWhere(" and SERV_ID = 'SY_COMM_ZHIDAO_PERSON_FOLLOW' and DATA_ID = '" + userId + "'");
            columnName = "USER_CODE";
        }
        followQuery.setAct(ServMgr.ACT_QUERY);

        OutBean followOutBean = ServMgr.act(followQuery);
        List<Bean> followList = followOutBean.getDataList();
        if (followList.size() > 0) {
            for (int i = 0; i < followList.size(); i++) {
                Bean followUser = followList.get(i);
                String followUserId = followUser.getStr(columnName);
                followUser.set("user", UserMgr.getUser(followUserId));
                followUser.set("followCount", getUserFollowCount(new ParamBean().set("userId", followUserId))
                        .getCount());
                followUser.set("answerCount", getAnswerCount(new ParamBean().set("userId", followUserId)).getCount());
                followUser.set("askCount", getAskCount(new ParamBean().set("userId", followUserId)).getCount());
                followUser.set("likeVoteCount", getLikeVoteCount(new ParamBean().set("userId", followUserId)));
            }
        }
        return followOutBean;
    }

    /**
     * 获取目标栏目下的子栏目和每个栏目下的专家数
     * @param paramBean - 参数bean
     * @return outbean
     */
    /*
     * public OutBean getSubChannelAndCount(ParamBean paramBean) { ChannelServ channelServ = new ChannelServ();
     * List<Bean> chanList = channelServ.getSubChannel(paramBean).getDataList(); for (int i = 0; i < chanList.size();
     * i++) { chanList.get(i).set("specCount", getSpecCountByChannel(new ParamBean().set("CHNL_ID",
     * chanList.get(i).getStr("CHNL_ID")))); } return new OutBean().setData(chanList); }
     */

    /**
     * 根据当前的chnlId获取这个领域的专家数量
     * @param paramBean - chnlId
     * @return - 专家数量
     */
    public int getSpecCountByChannel(ParamBean paramBean) {
        String chnlId = paramBean.getStr("CHNL_ID");

        // Bean daoQuery = new Bean();
        // daoQuery.set(Constant.PARAM_SELECT, " distinct (SPEC_ID)");
        // daoQuery.set(Constant.PARAM_WHERE, " and CHNL_ID='" + chnlId + "' or CHNL_PID='"
        // + chnlId + "'");
        // Bean bean = ServDao.(ServMgr.SY_COMM_ZHIDAO_SPEC_SUBJECT, daoQuery);
        // return 1;

        ParamBean query = new ParamBean();
        query.setServId(ServMgr.SY_COMM_ZHIDAO_SPEC_SUBJECT);
        query.setAct(ServMgr.ACT_QUERY);
        query.setQueryExtWhere(" and CHNL_ID='" + chnlId + "' or CHNL_PID='"
                + chnlId + "'");
        query.setSelect("DISTINCT(SPEC_ID)");
        OutBean outBean = ServMgr.act(query);
        return outBean.getCount();
    }

    /**
     * 获取同事分享信息
     * @param paramBean - 传入参数，暂无
     * @return - 返回结果
     */
    public OutBean getWorkmateShare(ParamBean paramBean) {
        // 哪个页面调用的这个方法，如果是recommend页面的"同事分享"将去掉userId的过滤条件
        ParamBean query = new ParamBean();
        // 获取参数
        String pageName = paramBean.getStr("pageName");
        int count = paramBean.getInt("count");
        int page = paramBean.getInt("page");
        // 判断是否应该添加过滤条件
        if (!("recommend".equals(pageName))) {
            UserBean currentUser = Context.getUserBean();
            String userId = currentUser.getCode();
            query.setQueryExtWhere(" and TARGET_USER='" + userId + "'");
        }
        query.setServId(ServMgr.SY_COMM_ZHIDAO_SHARE);
        query.setAct(ServMgr.ACT_QUERY);
        if (page > 0) {
            query.setQueryPageNowPage(page);
        } else {
            query.setQueryPageNowPage(1);
        }
        if (count > 0) {
            query.setQueryPageShowNum(count);
        } else {
            query.setQueryPageShowNum(5);
        }
        query.setOrder("S_ATIME DESC");
        OutBean outBean = ServMgr.act(query);
        List<Bean> shareList = outBean.getDataList();
        for (int i = 0; i < shareList.size(); i++) {
            Bean share = shareList.get(i);
            if ("SY_COMM_ZHIDAO_ANSWER".equals(share.getStr("SERV_ID"))) {
                OutBean answerOutBean = ServMgr.act(ServMgr.SY_COMM_ZHIDAO_ANSWER, ServMgr.ACT_BYID,
                        new ParamBean().setId(share.getStr("DATA_ID")));
                String qId = answerOutBean.getStr("Q_ID");
                shareList.get(i).set("Q_ID", qId);
            } else {
                shareList.get(i).set("Q_ID", shareList.get(i).getStr("DATA_ID"));
            }
        }

        return outBean;
    }

    /**
     * 获取知道用户的简介信息
     * @param param - 参数
     * @return - 返回值
     */
    public String getZhidaoUserDesc(ParamBean param) {
        String userId = param.getStr("USER_ID");
        Bean bean = new Bean();
        bean.set("USER_ID", userId);
        Bean zhidaoUserBean = ServDao.find(ServMgr.SY_COMM_ZHIDAO_USER, bean);
        if (zhidaoUserBean == null) {
            return "此人暂无简介！";
        }
        return zhidaoUserBean.getStr("USER_DESC");
    }

    /**
     * 获取知道用户的擅长领域
     * @param param - 参数
     * @return - 返回值
     */
    public OutBean getZhidaoUserSubject(ParamBean param) {
        String userId = param.getStr("USER_ID");
        ParamBean query = new ParamBean(ServMgr.SY_COMM_ZHIDAO_USER_SUBJECT, ServMgr.ACT_FINDS);
        query.setWhere(" and USER_ID = '" + userId + "'");
        return ServMgr.act(query);
    }

    /**
     * 获取用户的信息包括公司名称(不是知道用户)
     * @param param - 参数
     * @return - 返回值
     */
    public UserBean getZhidaoUser(ParamBean param) {
        String userId = param.getStr("userId");
        UserBean userBean = UserMgr.getUser(userId);
        userBean.set("CMPY_NAME", userBean.getCmpyName());
        return userBean;
    }

}
