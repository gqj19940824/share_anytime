package com.unity.configuration;//package com.unity.configuration;
//
//import com.netflix.zuul.ZuulFilter;
//import com.netflix.zuul.context.RequestContext;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//public class AccessFilter extends ZuulFilter {
//    @Autowired
//    HttpServletRequest httpServletRequest;
//    @Autowired
//    HttpServletResponse httpServletResponse;
//
//    @Override
//    public boolean shouldFilter() {
//        return false;
//    }
//
//    @Override
//    public Object run() {
//        RequestContext ctx = RequestContext.getCurrentContext();
//        String sessionId = httpServletRequest.getSession().getId();
//        System.out.println("sessionId==============="+sessionId);
//        ctx.addZuulRequestHeader("Cookie", "SESSION=" + sessionId);
//        ctx.setSendZuulResponse(true);// �Ը��������·��
//        ctx.setResponseStatusCode(200); // ����200��ȷ��Ӧ
//        return ctx;
//    }
//
//    @Override
//    public String filterType() {
//        return null;
//    }
//
//    @Override
//    public int filterOrder() {
//        return 0;
//    }
//}