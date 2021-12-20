package com.chainverse.sdk.base.web3;

import android.content.Context;

import com.chainverse.sdk.common.Constants;
import com.chainverse.sdk.common.LogUtil;
import com.chainverse.sdk.common.EncryptPreferenceUtils;
import com.chainverse.sdk.common.WalletUtils;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class BaseWeb3 {
    private static BaseWeb3 instance;
    private Web3j web3;
    private Context context;
    public static BaseWeb3 getInstance(){
        if(instance == null){
            synchronized (EncryptPreferenceUtils.class){
                if(instance == null){
                    instance = new BaseWeb3();
                }
            }
        }
        return instance;
    }


    public BaseWeb3 init(Context context){
        web3 = Web3j.build(new HttpService(Constants.URL.urlBlockchain));
        this.context = context;
        return instance;
    }

    public EthCall callFunction(String contractAddress, String method, List<Type> inputParameters) throws Exception{
        Function function = new Function(method, inputParameters, Collections.emptyList());
        String encodedFunction = FunctionEncoder.encode(function);
        EthCall result = web3.ethCall(Transaction.createEthCallTransaction(null, contractAddress, encodedFunction), DefaultBlockParameter.valueOf("latest")).sendAsync().get();
        return result;
    }

    public BigDecimal getBalance(String address) throws Exception{
        EthGetBalance ethGetBalance  = web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync().get();
        BigDecimal balance = Convert.fromWei(ethGetBalance.getBalance().toString(), Convert.Unit.ETHER);
        return  balance;
    }

    public String transfer(String to, BigDecimal amount) throws Exception {
        BigDecimal amountInWei = Convert.toWei(amount, Convert.Unit.ETHER);
        TransactionReceipt transactionReceipt = Transfer.sendFunds(web3, WalletUtils.getInstance().init(context).getCredential(), to, amountInWei, Convert.Unit.WEI).send();
        return transactionReceipt.getTransactionHash();
    }

}
