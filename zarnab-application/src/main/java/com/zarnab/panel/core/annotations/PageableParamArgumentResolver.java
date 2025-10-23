package com.zarnab.panel.core.annotations;

import com.zarnab.panel.core.dto.req.FilterRequest;
import com.zarnab.panel.core.dto.req.PageableRequest;
import com.zarnab.panel.core.dto.req.SortRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.ArrayList;
import java.util.List;

public class PageableParamArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(PageableParam.class) != null
                && parameter.getParameterType().equals(PageableRequest.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        PageableParam pageableParam = parameter.getParameterAnnotation(PageableParam.class);
        boolean required = pageableParam != null && pageableParam.required();

        String pageStr = webRequest.getParameter("page");
        int page = 0;
        if (pageStr != null) {
            page = Integer.parseInt(pageStr);
        } else if (required) {
            throw new IllegalArgumentException("Required parameter 'page' is not present.");
        }

        String sizeStr = webRequest.getParameter("size");
        int size = 10;
        if (sizeStr != null) {
            size = Integer.parseInt(sizeStr);
        } else if (required) {
            throw new IllegalArgumentException("Required parameter 'size' is not present.");
        }

        String[] filtersStr = webRequest.getParameterValues("filters");
        List<FilterRequest> filterRequests = new ArrayList<>();
        if (filtersStr != null) {
            for (String filter : filtersStr) {
                String[] parts = filter.split(":");
                if (parts.length >= 3) {
                    FilterRequest.Operator operator = FilterRequest.Operator.valueOf(parts[1].toUpperCase());
                    String value = parts[2];
                    String valueTo = (parts.length > 3) ? parts[3] : null;
                    filterRequests.add(new FilterRequest(parts[0], operator, value, valueTo));
                }
            }
        }

        String[] sortsStr = webRequest.getParameterValues("sorts");
        List<SortRequest> sortRequests = new ArrayList<>();
        if (sortsStr != null) {
            for (String sort : sortsStr) {
                String[] parts = sort.split(":");
                if (parts.length == 2) {
                    SortRequest.Direction direction = SortRequest.Direction.valueOf(parts[1].toUpperCase());
                    sortRequests.add(new SortRequest(parts[0], direction));
                }
            }
        }

        return new PageableRequest(page, size, filterRequests, sortRequests);
    }
}
