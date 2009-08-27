package br.com.bluesoft.pronto.util;

import javax.servlet.http.HttpServletResponse;

public class ControllerUtil {

	public static final String ENCODING_UTF8 = "UTF-8";

	public static void writeText(final HttpServletResponse response, final Object text) {
		try {
			response.setContentType("text/plain");
			response.setCharacterEncoding(ENCODING_UTF8);
			response.getWriter().print(text);
		} catch (final Exception e) {
			throw new RuntimeException("N�o foi poss�vel gerar a resposta JSON.", e);
		}
	}

}
