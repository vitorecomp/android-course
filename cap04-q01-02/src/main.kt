//LogTempo.kt
inline fun logTempo(funcao:()->Unit){
    val antes = System.currentTimeMillis()
    funcao()
    val depois = System.currentTimeMillis()
    println("Tempo de execucao ${depois-antes}ms")
}

open class Lancamento(val valor:Double, val descricao:String){
    open fun formatoDeExtrato():String{
        return "(Lancamento) ${this.descricao}\n\t\t\t${this.valor}"
    }
}

class LancamentoDebito(valor:Double, descricao:String) : Lancamento(valor, descricao) {
    override fun formatoDeExtrato(): String {
        return "(C) ${this.descricao}\n\t\t\t${this.valor}"
    }
}

class LancamentoCredito(valor:Double, descricao:String) : Lancamento(valor, descricao){
    override fun formatoDeExtrato():String{
        return "(D) ${this.descricao}\n\t\t\t${this.valor}"
    }
}

class Conta(val descricao:String){
    private val lancamentos: MutableList<Lancamento> = mutableListOf()
    var saldo: Double = 0.0
        private set

    fun novoLancamento(lancamento:Lancamento){
        this.lancamentos.add(lancamento)
        this.saldo += lancamento.valor
    }

    fun mostrarLancamentos(filter:(Lancamento) -> Boolean) {
        for(lancamento in this.lancamentos){
            if(filter(lancamento))
                println("${lancamento.formatoDeExtrato()}")
        }
    }
}

class MovimentoDiario(var descricao: String){
    private var contaCorrente:Conta = Conta(descricao)


    fun realizaMovimentoDiario(){
        val credito01 = LancamentoCredito(4000.0, "Salário")

        val debito01 = LancamentoDebito(-100.0, "Agua")
        val debito02 = LancamentoDebito(-200.0, "Luz")
        val debito03 = LancamentoDebito(-1000.0, "Aluguel")

        contaCorrente.novoLancamento(credito01)
        contaCorrente.novoLancamento(debito01)
        contaCorrente.novoLancamento(debito02)
        contaCorrente.novoLancamento(debito03)
    }

    fun mostraBalanco() {
        contaCorrente.mostrarLancamentos(){
            it.valor < 0
        };
        println("Saldo final: ${contaCorrente.saldo}")
    }

}

fun main(args: Array<String>) {
    logTempo {
        val mov = MovimentoDiario("Conta Corrente 1111/111111")
        mov.realizaMovimentoDiario()
        mov.mostraBalanco()
    }
}
