// Generated from GyhLang.g4 by ANTLR 4.7.2
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link GyhLangParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface GyhLangVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link GyhLangParser#programa}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrograma(GyhLangParser.ProgramaContext ctx);
	/**
	 * Visit a parse tree produced by {@link GyhLangParser#listaDeclaracoes}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListaDeclaracoes(GyhLangParser.ListaDeclaracoesContext ctx);
	/**
	 * Visit a parse tree produced by {@link GyhLangParser#declaracao}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclaracao(GyhLangParser.DeclaracaoContext ctx);
	/**
	 * Visit a parse tree produced by {@link GyhLangParser#tipoVar}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTipoVar(GyhLangParser.TipoVarContext ctx);
	/**
	 * Visit a parse tree produced by {@link GyhLangParser#listaComandos}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListaComandos(GyhLangParser.ListaComandosContext ctx);
	/**
	 * Visit a parse tree produced by {@link GyhLangParser#comando}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComando(GyhLangParser.ComandoContext ctx);
	/**
	 * Visit a parse tree produced by {@link GyhLangParser#comandoAtribuicao}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComandoAtribuicao(GyhLangParser.ComandoAtribuicaoContext ctx);
	/**
	 * Visit a parse tree produced by {@link GyhLangParser#comandoEntrada}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComandoEntrada(GyhLangParser.ComandoEntradaContext ctx);
	/**
	 * Visit a parse tree produced by {@link GyhLangParser#comandoSaida}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComandoSaida(GyhLangParser.ComandoSaidaContext ctx);
	/**
	 * Visit a parse tree produced by {@link GyhLangParser#comandoCondicao}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComandoCondicao(GyhLangParser.ComandoCondicaoContext ctx);
	/**
	 * Visit a parse tree produced by {@link GyhLangParser#comandoRepeticao}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComandoRepeticao(GyhLangParser.ComandoRepeticaoContext ctx);
	/**
	 * Visit a parse tree produced by {@link GyhLangParser#subAlgoritmo}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubAlgoritmo(GyhLangParser.SubAlgoritmoContext ctx);
	/**
	 * Visit a parse tree produced by {@link GyhLangParser#expressaoAritmetica}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressaoAritmetica(GyhLangParser.ExpressaoAritmeticaContext ctx);
	/**
	 * Visit a parse tree produced by {@link GyhLangParser#termoAritmetico}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTermoAritmetico(GyhLangParser.TermoAritmeticoContext ctx);
	/**
	 * Visit a parse tree produced by {@link GyhLangParser#fatorAritmetico}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFatorAritmetico(GyhLangParser.FatorAritmeticoContext ctx);
	/**
	 * Visit a parse tree produced by {@link GyhLangParser#expressaoRelacional}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressaoRelacional(GyhLangParser.ExpressaoRelacionalContext ctx);
	/**
	 * Visit a parse tree produced by {@link GyhLangParser#termoRelacional}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTermoRelacional(GyhLangParser.TermoRelacionalContext ctx);
	/**
	 * Visit a parse tree produced by {@link GyhLangParser#operadorBooleano}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperadorBooleano(GyhLangParser.OperadorBooleanoContext ctx);
}