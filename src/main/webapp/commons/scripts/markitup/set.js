mySettings = {
	previewParserPath:	'../wiki/parse.action', // path to your Wiki parser
	onShiftEnter:		{keepDefault:false, replaceWith:'\n\n'},
	markupSet: [
		{name:'T�tulo 1', key:'1', openWith:'== ', closeWith:' ==', placeHolder:'Seu t�tulo aqui...' },
		{name:'T�tulo 2', key:'2', openWith:'=== ', closeWith:' ===', placeHolder:'Seu t�tulo aqui...' },
		{name:'T�tulo 3', key:'3', openWith:'==== ', closeWith:' ====', placeHolder:'Seu t�tulo aqui...' },
		{name:'T�tulo 4', key:'4', openWith:'===== ', closeWith:' =====', placeHolder:'Seu t�tulo aqui...' },
		{name:'T�tulo 5', key:'5', openWith:'====== ', closeWith:' ======', placeHolder:'Seu t�tulo aqui...' },
		{separator:'---------------' },		
		{name:'Negrito', key:'B', openWith:"'''", closeWith:"'''"}, 
		{name:'It�lico', key:'I', openWith:"''", closeWith:"''"}, 
		{name:'Riscado', key:'S', openWith:'<s>', closeWith:'</s>'}, 
		{separator:'---------------' },
		{name:'Lista de Bullets', openWith:'(!(* |!|*)!)'}, 
		{name:'Lista Num�rica', openWith:'(!(# |!|#)!)'}, 
		{separator:'---------------' },
		//{name:'Picture', key:"P", replaceWith:'[[Image:[![Url:!:http://]!]|[![name]!]]]'}, 
		{name:'Link', key:"L", openWith:"[[![Link]!] ", closeWith:']', placeHolder:'Your text to link here...' },
		{name:'Url', openWith:"[[![Url:!:http://]!] ", closeWith:']', placeHolder:'Your text to link here...' },
		{separator:'---------------' },
		{name:'Quotes', openWith:'(!(> |!|>)!)', placeHolder:''},
		{name:'C�digo Fonte', openWith:'(!(<source lang="[![Language:!:php]!]">|!|<pre>)!)', closeWith:'(!(</source>|!|</pre>)!)'}, 
		{separator:'---------------' },
		{name:'Preview', call:'preview', className:'preview'}
	]
}
