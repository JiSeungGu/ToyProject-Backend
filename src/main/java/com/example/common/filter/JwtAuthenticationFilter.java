package com.example.common.filter;

import lombok.RequiredArgsConstructor;

import javax.servlet.*;
import java.io.IOException;

/**
 * packageName   : com.example.common.filter
 * fileName  : JwtAuthenticationFilter
 * author    : jiseung-gu
 * date  : 2023/06/26
 * description :
 *
 * */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilter {

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

  }

  @Override
  public void destroy() {
    super.destroy();
  }
}
