package com.chainverse.sdk.blockchain;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.chainverse.sdk.base.web3.BaseWeb3;
import com.chainverse.sdk.common.BroadcastUtil;
import com.chainverse.sdk.common.Constants;
import com.chainverse.sdk.common.LogUtil;
import com.chainverse.sdk.common.WalletUtils;
import com.chainverse.sdk.listener.Action;
import com.chainverse.sdk.manager.ServiceManager;
import com.chainverse.sdk.model.TransactionData;
import com.chainverse.sdk.ui.ChainverseSDKActivity;
import com.esaulpaugh.headlong.abi.Tuple;
import com.esaulpaugh.headlong.util.FastHex;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint128;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint64;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Contract {
    // Param type
    public static final String STRING = "string";
    public static final String ADDRESS = "address";
    public static final String UINT256 = "uint256";
    public static final String UINT8 = "uint8";
    public static final String UINT64 = "uint64";
    public static final String UINT128 = "uint128";
    public static final String BYTES32 = "bytes32";
    public static final String BOOL = "bool";
    public static final String TUPLE = "tuple";

    // State Mutability
    public static final String CONSTRUCTOR = "constructor";
    public static final String NONPAYABLE = "nonpayable";
    public static final String PAYABLE = "payable";
    public static final String VIEW = "view";

    // Function type
    public static final String EVENT = "event";
    public static final String FUNCTION = "function";

    private Context mContext;
    private String abi;
    private String contractAddress;
    private TransactionData transactionData;

    Web3j web3;

    protected Contract(Context mContext, String abi, String contractAddress) {
        this.mContext = mContext;
        this.abi = abi;
        this.contractAddress = contractAddress;
        if (ServiceManager.getInstance().init(mContext).getRPC() != null) {
            web3 = Web3j.build(new HttpService(ServiceManager.getInstance().init(mContext).getRPC()));
        }
    }

    public Contract(Context mContext, String abi, String contractAddress, TransactionData transactionData) {
        this.mContext = mContext;
        this.abi = abi;
        this.contractAddress = contractAddress;
        this.transactionData = transactionData;
        if (ServiceManager.getInstance().init(mContext).getRPC() != null) {
            web3 = Web3j.build(new HttpService(ServiceManager.getInstance().init(mContext).getRPC()));
        }
    }

    public static Contract load(Context context, String abi, String contractAddress) {
        return new Contract(context, abi, contractAddress);
    }

    public static Contract load(Context mContext, String abi, String contractAddress, TransactionData transactionData) {
        return new Contract(mContext, abi, contractAddress, transactionData);
    }

    public List callContract(String nameFunction, Object[] args) throws Exception {
        return executeContract(nameFunction, "", args, BigInteger.ZERO);
    }

    public List callContract(String nameFunction, Object[] args, BigInteger value) throws Exception {
        return executeContract(nameFunction, "", args, value);
    }

    public List callContract(String nameFunction, String typeInputs, Object[] args) throws Exception {
        return executeContract(nameFunction, typeInputs, args, BigInteger.ZERO);
    }

    public List callContract(String nameFunction, String typeInputs, Object[] args, BigInteger value) throws Exception {
        return executeContract(nameFunction, typeInputs, args, value);
    }

    private List executeContract(String nameFunction, String typeInputs, Object[] args, BigInteger value) throws Exception {
        JSONObject usedAbi = null;
        List data = new ArrayList();
        try {
            JSONArray jsonArray = new JSONArray(abi);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.has("name") && jsonObject.getString("name").equals(nameFunction)) {
                    if (typeInputs.isEmpty()) {
                        usedAbi = jsonArray.getJSONObject(i);
                        break;
                    } else {
                        String[] types = typeInputs.replaceAll("\\(|\\)", "").split(",");
                        boolean check = false;
                        for (int m = 0; m < jsonObject.getJSONArray("inputs").length(); m++) {
                            JSONObject input = jsonObject.getJSONArray("inputs").getJSONObject(m);
                            if (input.has("type") && input.getString("type").toLowerCase().equals(types[m].toLowerCase())) {
                                check = true;
                            } else {
                                check = false;
                            }
                        }
                        if (check) {
                            usedAbi = jsonArray.getJSONObject(i);
                            break;
                        }
                    }
                }
            }

            if (usedAbi != null) {
                if (usedAbi.getString("stateMutability").equals(VIEW)) {
                    data = view(usedAbi, nameFunction, args);
                }
                if (usedAbi.getString("stateMutability").equals(NONPAYABLE) || usedAbi.getString("stateMutability").equals(PAYABLE)) {
                    payable(usedAbi, nameFunction, args, value);
                }
                return data;
            } else {
                throw new Exception("The function can not be found");
            }
        } catch (JSONException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    private void payable(JSONObject abi, String nameFunction, Object[] args, BigInteger value) {
        showLoading(() -> {
            WalletUtils walletUtils = WalletUtils.getInstance().init(mContext);
            try {
                Function function = new Function(nameFunction, convertTypeParam(abi.getJSONArray("inputs"), args), Collections.emptyList());
                String functionEncoder = FunctionEncoder.encode(function);

                TransactionData transactionData = this.transactionData;
                if (transactionData == null || transactionData.getData() == null || transactionData.getData().isEmpty()) {
                    String data = "0x" + functionEncoder;
                    BigInteger gasPrice = web3.ethGasPrice().sendAsync().get().getGasPrice();
                    BigInteger nonce = getNonce();
                    BigInteger gasLimit = getGasLimit(value, Constants.CONTRACT.MarketService, functionEncoder);

                    transactionData = new TransactionData(
                            nonce.longValue(), gasPrice.longValue(), gasLimit.longValue(),
                            value.longValue(), data, contractAddress, walletUtils.getAddress(),
                            Constants.EFunction.callContract, "");
                }
                BroadcastUtil.send(mContext, Constants.ACTION.DIMISS_LOADING);

                Intent intent = new Intent(mContext, ChainverseSDKActivity.class);
                intent.putExtra("transactionData", transactionData);
                intent.putExtra("screen", Constants.SCREEN.CONFIRM_TRANSACTION);

                mContext.startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            BroadcastUtil.send(mContext, Constants.ACTION.DIMISS_LOADING);
        });
    }

    private List view(JSONObject abi, String nameFunction, Object[] args) throws Exception {
        List data = new ArrayList();
        com.esaulpaugh.headlong.abi.Function f = com.esaulpaugh.headlong.abi.Function.fromJson(abi.toString());
        EthCall ethCall = BaseWeb3.getInstance().init(mContext).callFunction(contractAddress, nameFunction, convertTypeParam(abi.getJSONArray("inputs"), args));

        Tuple decoded = f.decodeReturn(
                FastHex.decode(ethCall.getValue().replaceFirst("0x", ""))
        );
        for (int i = 0; i < decoded.size(); i++) {
            data.add(decoded.get(i));
        }
        return data;
    }

    private BigInteger getNonce() {
        BigInteger nonce = BigInteger.ONE;

        try {
            String address = WalletUtils.getInstance().init(mContext).getAddress();
//            EthBlock blockNumber = web3.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).sendAsync().get();
//            BigInteger block = blockNumber.getBlock().getNumber().add(new BigInteger("5"));

            EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(
                    address, DefaultBlockParameterName.LATEST).sendAsync().get();

            nonce = ethGetTransactionCount.getTransactionCount();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return nonce;
    }

    private BigInteger getGasLimit(BigInteger value, String to, String data) throws Exception {
        BigInteger gasLimit;
        String address = WalletUtils.getInstance().init(mContext).getAddress();
        try {

            Transaction transaction = Transaction.createFunctionCallTransaction(address, getNonce(), null, null, to, value, data);
            Request<?, EthEstimateGas> rs = web3.ethEstimateGas(transaction);
            EthEstimateGas eGasLimit = rs.sendAsync().get();
            if (eGasLimit.hasError()) {
                throw new Exception(eGasLimit.getError().getMessage());
            } else {
                gasLimit = eGasLimit.getAmountUsed();
            }
        } catch (Exception e) {
            throw e;
        }
        return gasLimit;
    }

    private List convertTypeParam(JSONArray inputs, Object[] args) {
        List params = new ArrayList();
        for (int i = 0; i < inputs.length(); i++) {
            try {
                JSONObject input = inputs.getJSONObject(i);
                switch (input.getString("type")) {
                    case ADDRESS:
                        params.add(new Address(160, (String) args[i]));
                        break;
                    case UINT8:
                        params.add(new Uint8((BigInteger) args[i]));
                        break;
                    case UINT64:
                        params.add(new Uint64((BigInteger) args[i]));
                        break;
                    case UINT128:
                        params.add(new Uint128((BigInteger) args[i]));
                        break;
                    case UINT256:
                        params.add(new Uint256((BigInteger) args[i]));
                        break;
                    case BYTES32:
                        params.add(new Bytes32((byte[]) args[i]));
                        break;
                    case BOOL:
                        params.add(new Bool((Boolean) args[i]));
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return params;
    }

    private void showLoading(Action.Callback callback) {
        Intent intent = new Intent(mContext, ChainverseSDKActivity.class);
        intent.putExtra("screen", Constants.SCREEN.LOADING);
        mContext.startActivity(intent);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onCallback();
            }
        }, 500);
    }
}
