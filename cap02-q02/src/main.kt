//TesteFuncao.kt
fun main(args: Array<String>){
    for(i in 2000..2500){
        if(ehAnoBissexto(i)){
            println("O ano $i eh bissexto")
        }
    }
}
fun ehAnoBissexto(ano: Int): Boolean {
    if(ano%400 == 0)
        return true
    if(ano%4 == 0 && ano%100 != 0 )
        return true
    return false
}