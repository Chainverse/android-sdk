package com.chainverse.sdk.common;

public class Constants {
    public interface CONTRACT {
        public static String ChainverseFactory = "0x640Dce1028b1111421b72b884320D0e93d974fB3";
        public static String MarketService = "0x2ccA92F66BeA2A7fA2119B75F3e5CB698C252564";
    }

    public interface URL {
        public static String urlSocket = "wss://beta-sdk-api.dev.chainverse.xyz";
        public static String urlRestful = "https://beta-sdk-api.dev.chainverse.xyz";
        public static String urlResfulMarket = "https://beta-faucet-api.dev.chainverse.xyz";
        public static String urlBuyTest = "https://beta-game-sign-2.dev.chainverse.xyz";
        public static String urlBlockchain = "https://data-seed-prebsc-1-s2.binance.org:8545";
    }

//    rpc: [
//            "https://data-seed-prebsc-1-s1.binance.org:8545",
//            "https://data-seed-prebsc-1-s3.binance.org:8545",
//            "https://data-seed-prebsc-2-s1.binance.org:8545",
//            "https://data-seed-prebsc-1-s2.binance.org:8545",
//            "https://data-seed-prebsc-2-s2.binance.org:8545",
//            ]

    public interface ACTION {
        public static String CREATED_WALLET = "com.chainverse.action.created.wallet";
        public static String DIMISS_LOADING = "com.chainverse.action.dimiss.loading";
    }

    public interface SCREEN {
        public static String CONNECT_VIEW = "com.chainverse.screen.view.connect";
        public static String CONFIRM_SIGN = "com.chainverse.screen.view.confirm.sign";
        public static String WALLET = "com.chainverse.screen.view.wallet";
        public static String WALLET_INFO = "com.chainverse.screen.view.wallet.info";
        public static String RECOVERY_WALLET = "com.chainverse.screen.view.recovery.wallet";
        public static String EXPORT_WALLET = "com.chainverse.screen.view.export.wallet";
        public static String CREATE_WALLET = "com.chainverse.screen.view.create.wallet";
        public static String IMPORT_WALLET = "com.chainverse.screen.view.import.wallet";
        public static String BACKUP_WALLET = "com.chainverse.screen.view.backup.wallet";
        public static String VERIFY_WALLET = "com.chainverse.screen.view.verify.wallet";
        public static String ALERT = "com.chainverse.screen.view.alert";
        public static String LOADING = "com.chainverse.screen.view.loading";
        public static String BUY_NFT = "com.chainverse.screen.view.buyNFT";
    }

    public interface TOKEN_SUPPORTED {
        public static String USDT = "0x337610d27c682E347C9cD60BD4b3b107C9d34dDd";
        public static String BUSD = "0xed24fc36d5ee211ea25a80239fb8c4cfd80f12ee";
        public static String CVT = "0x672021e3c741910896cad6D6121446a328ba5634";
        public static String NativeCurrency = "0x0000000000000000000000000000000000000000";
        public static final String[] TOKENS = {
                "0x0000000000000000000000000000000000000000", //BNB
                "0x337610d27c682E347C9cD60BD4b3b107C9d34dDd",//USDT
                "0xed24fc36d5ee211ea25a80239fb8c4cfd80f12ee",//BUSD
                "0x672021e3c741910896cad6D6121446a328ba5634",//CTV
        };
    }
}
