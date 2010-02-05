package br.com.bluesoft.pronto.util;

public class StringUtil {

	public static String retiraAcentuacao(final String in) {

		if (in == null || in.length() < 1) {
			return in;
		}

		String out = in;
		final char[] comAcento = new char[] { '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�' };
		final char[] semAcento = new char[] { 'c', 'a', 'a', 'a', 'a', 'a', 'e', 'e', 'e', 'e', 'i', 'i', 'i', 'i', 'o', 'o', 'o', 'o', 'o', 'u', 'u', 'u', 'u', 'A' };
		for (int i = 0; i < comAcento.length; i++) {
			out = out.replace(Character.toLowerCase(comAcento[i]), Character.toLowerCase(semAcento[i])).replace(Character.toUpperCase(comAcento[i]), Character.toUpperCase(semAcento[i]));
		}
		return out;
	}
}
