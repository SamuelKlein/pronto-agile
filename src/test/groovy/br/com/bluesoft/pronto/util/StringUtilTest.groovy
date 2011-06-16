package br.com.bluesoft.pronto.util

import org.junit.*
import static org.junit.Assert.*

class StringUtilTest {
	
	@Test
	@Ignore
	void retiraAcentuacao() {
		assertEquals 'x', StringUtil.retiraAcentuacao('x')
		assertEquals 'Andre', StringUtil.retiraAcentuacao('Andr�')
		assertEquals 'Descricao', StringUtil.retiraAcentuacao('Descri��o')
		assertEquals 'titulo da tarefa', StringUtil.retiraAcentuacao('t�tulo da tarefa')
	}
	
}