package com.mimao.kmp.walletconnect.utils


class WCLogger private constructor(){
    private var turnOn: Boolean = false

    internal fun log(msg: String) {
        if (turnOn) {
           println("KMP-WalletConnect===>$msg")
        }
    }

    companion object {
        private val logger by lazy {
            WCLogger()
        }

        internal fun log(msg: String){
            logger.log(msg)
        }

        fun switch(on: Boolean) {
            logger.turnOn = on
        }
    }
}

