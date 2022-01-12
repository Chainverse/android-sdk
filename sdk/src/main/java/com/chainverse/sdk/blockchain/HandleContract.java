package com.chainverse.sdk.blockchain;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.tuples.Tuple;
import org.web3j.tuples.generated.Tuple10;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tuples.generated.Tuple5;
import org.web3j.tuples.generated.Tuple6;
import org.web3j.tuples.generated.Tuple7;
import org.web3j.tuples.generated.Tuple8;
import org.web3j.tuples.generated.Tuple9;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

public class HandleContract extends Contract {
    private static final String BINARY = "";

    // Param type
    private static final String STRING = "string";
    private static final String ADDRESS = "address";
    private static final String UINT256 = "uint256";
    private static final String BOOL = "bool";
    private static final String TUPLE = "tuple";

    // State Mutability
    private static final String CONSTRUCTOR = "constructor";
    private static final String NONPAYABLE = "nonpayable";
    private static final String PAYABLE = "payable";
    private static final String VIEW = "view";

    // Function type
    private static final String EVENT = "event";
    private static final String FUNCTION = "function";

    String _abi;

    @Deprecated
    protected HandleContract(String contractAddress, String abi, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
        this._abi = abi;
    }

    protected HandleContract(String contractAddress, String abi, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
        this._abi = abi;
    }

    @Deprecated
    protected HandleContract(String contractAddress, String abi, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
        this._abi = abi;
    }

    protected HandleContract(String contractAddress, String abi, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
        this._abi = abi;
    }

    @Deprecated
    public static HandleContract load(String contractAddress, String abi, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new HandleContract(contractAddress, abi, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static HandleContract load(String contractAddress, String abi, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new HandleContract(contractAddress, abi, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static HandleContract load(String contractAddress, String abi, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new HandleContract(contractAddress, abi, web3j, credentials, contractGasProvider);
    }

    public static HandleContract load(String contractAddress, String abi, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new HandleContract(contractAddress, abi, web3j, transactionManager, contractGasProvider);
    }

    public static HandleContract load(String contractAddress, String abi, Web3j web3j, Credentials credentials) {
        return new HandleContract(contractAddress, abi, web3j, credentials, new DefaultGasProvider());
    }

    public RemoteFunctionCall callFunc(String func, List inputs) {
        RemoteFunctionCall remoteCall = null;
        if (!_abi.isEmpty()) {
            try {
                JSONArray abis = new JSONArray(_abi);
                int i = 0;
                while (i < abis.length()) {
                    JSONObject abi = abis.getJSONObject(i);
                    if (abi.has("name") && abi.getString("name").toUpperCase().equals(func.toUpperCase())) {
                        if (abi.getJSONArray("inputs").length() == inputs.size()) {
                            break;
                        }
                    }
                    i++;
                }

                if (i < abis.length()) {
                    remoteCall = handleFunction(abis.getJSONObject(i), inputs);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return remoteCall;
    }

    public RemoteFunctionCall callFunc(String func, List inputs, List<TypeReference<?>> outputParameters) {
        RemoteFunctionCall remoteCall = null;
        if (!_abi.isEmpty()) {
            try {
                JSONArray abis = new JSONArray(_abi);
                int i = 0;
                while (i < abis.length()) {
                    JSONObject abi = abis.getJSONObject(i);
                    if (abi.has("name") && abi.getString("name").toUpperCase().equals(func.toUpperCase())) {
                        if (abi.getJSONArray("inputs").length() == inputs.size()) {
                            break;
                        }
                    }
                    i++;
                }
                if (i < abis.length()) {
                    remoteCall = handleFunction(abis.getJSONObject(i), inputs, outputParameters);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return remoteCall;
    }

    protected RemoteFunctionCall handleFunction(JSONObject abi, List inputs) {
        List inputParams = new ArrayList();
        List outputParams = Collections.emptyList();
        JSONArray outputObject = new JSONArray();
        RemoteFunctionCall output = null;
        try {
            if (abi.has("inputs")) {
                JSONArray inputObject = abi.getJSONArray("inputs");
                inputParams = handleInput(inputObject, inputs);
            }
            if (abi.has("outputs")) {
                outputObject = abi.getJSONArray("outputs");
                outputParams = handleOutput(outputObject);
            }

            if (abi.has("type") && abi.getString("type").equals(FUNCTION)) {
                if (abi.getString("stateMutability").equals(NONPAYABLE) || abi.getString("stateMutability").equals(PAYABLE)) {
                    output = nonpayable(abi.getString("name"), inputParams, outputParams);
                }
                if (abi.getString("stateMutability").equals(VIEW) && abi.has("outputs")) {
                    if (abi.getJSONArray("outputs").length() == 1) {
                        output = view(abi.getString("name"), inputParams, outputParams);
                    } else {
                        output = executeCallMultipleValueTuple(abi.getString("name"), inputParams, outputParams);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return output;
    }

    protected RemoteFunctionCall handleFunction(JSONObject abi, List inputs, List<TypeReference<?>> outputParameters) {
        List inputParams = new ArrayList();
        RemoteFunctionCall output = null;
        try {
            if (abi.has("inputs")) {
                JSONArray inputObject = abi.getJSONArray("inputs");
                inputParams = handleInput(inputObject, inputs);
            }

            if (abi.has("type") && abi.getString("type").equals(FUNCTION)) {
                if (abi.getString("stateMutability").equals(NONPAYABLE) || abi.getString("stateMutability").equals(PAYABLE)) {
                    output = nonpayable(abi.getString("name"), inputParams, outputParameters);
                }
                if (abi.getString("stateMutability").equals(VIEW) && abi.has("outputs")) {
                    if (abi.getJSONArray("outputs").length() == 1) {
                        output = view(abi.getString("name"), inputParams, outputParameters);
                    } else {
                        output = executeCallMultipleValueTuple(abi.getString("name"), inputParams, outputParameters);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return output;
    }

    protected RemoteFunctionCall nonpayable(String func, List inputs, List<TypeReference<?>> output) {
        final Function function = new Function(func, inputs, output);
        return executeRemoteCallTransaction(function);
    }

    protected RemoteFunctionCall view(String func, List inputs, List<TypeReference<?>> output) {
        TypeReference type = (TypeReference) output.get(0);
        final Function function = new Function(func, inputs, output);
        return executeRemoteCallSingleValueReturn(function, getClassType(type));
    }

    protected RemoteFunctionCall executeCallMultipleValueTuple(String func, List inputs, List<TypeReference<?>> outputs) {
        final Function function = new Function(func, inputs, outputs);

        return new RemoteFunctionCall(function,
                new Callable() {
                    @Override
                    public Tuple call() throws Exception {
                        List results = executeCallMultipleValueReturn(function);
                        return getTuple(outputs.size(), results);
                    }
                });
    }

    private List handleInput(JSONArray inputParams, List inputs) {
        List param = new ArrayList();
        try {
            for (int i = 0; i < inputParams.length(); i++) {
                JSONObject paramObject = inputParams.getJSONObject(i);
                switch (paramObject.getString("type")) {
                    case UINT256:
                        param.add(new Uint256((BigInteger) inputs.get(i)));
                        break;
                    case ADDRESS:
                        param.add(new Address(160, (String) inputs.get(i)));
                        break;
                    case STRING:
                        break;
                    case BOOL:
                        param.add(new Bool((boolean) inputs.get(i)));
                        break;
                    default:
                        break;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return param;
    }

    private List handleOutput(JSONArray inputParams) {
        List param = new ArrayList();
        try {
            for (int i = 0; i < inputParams.length(); i++) {
                JSONObject paramObject = inputParams.getJSONObject(i);
                switch (paramObject.getString("type")) {
                    case UINT256:
                        param.add(new TypeReference<Uint256>() {
                        });
                        break;
                    case ADDRESS:
                        param.add(new TypeReference<Address>() {
                        });
                        break;
                    case STRING:
                        param.add(new TypeReference<Utf8String>() {
                        });
                        break;
                    case BOOL:
                        param.add(new TypeReference<Bool>() {
                        });
                        break;
                    default:
                        break;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        param = (param.size() > 0) ? param : Collections.emptyList();
        return param;
    }

    @NotNull
    public static String formatAbi(String abi) {
        JSONArray arrayFormat = new JSONArray();
        try {
            JSONArray arrayAbi = new JSONArray(abi);

            for (int i = 0; i < arrayAbi.length(); i++) {
                JSONObject obj = arrayAbi.getJSONObject(i);
                JSONObject format = new JSONObject();

                if (obj.has("type")) {
                    format.put("type", obj.getString("type"));
                }

                if (obj.has("name")) {
                    format.put("name", obj.getString("name"));
                }

                if (obj.has("inputs")) {

                    JSONArray inputs = handleInputOutput(obj.getJSONArray("inputs"));
                    format.put("inputs", inputs);
                }

                if (obj.has("outputs")) {

                    JSONArray outputs = handleInputOutput(obj.getJSONArray("outputs"));
                    format.put("outputs", outputs);
                }

                arrayFormat.put(format);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayFormat.toString();
    }

    public static JSONArray handleInputOutput(JSONArray params) {
        JSONArray inputs = new JSONArray();

        for (int i = 0; i < params.length(); i++) {
            JSONObject input = new JSONObject();
            try {
                JSONObject inputAbi = params.getJSONObject(i);

                input.put("name", inputAbi.getString("name"));
                switch (inputAbi.getString("type")) {
                    case ADDRESS:
                    case STRING:
                        input.put("type", "string");
                        break;
                    case UINT256:
                        input.put("type", "bignumber");
                        break;
                    case BOOL:
                        input.put("type", "boolean");
                        break;
                    default:
                        break;
                }
                inputs.put(input);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return inputs;
    }

    protected Class getClassType(TypeReference type) {
        if (type.getType().toString().indexOf("Uint256") >= 0) {
            return BigInteger.class;
        } else if (type.getType().toString().indexOf("Bool") >= 0) {
            return Boolean.class;
        } else {
            return String.class;
        }
    }

    private Tuple getTuple(int size, List<Type> results) {


        switch (size) {
            case 2:
                return new Tuple2(results.get(0), results.get(1));
            case 3:
                return new Tuple3(results.get(0), results.get(1), results.get(2));
            case 4:
                return new Tuple4(results.get(0), results.get(1), results.get(2), results.get(3));
            case 5:
                return new Tuple5(results.get(0), results.get(1), results.get(2), results.get(3), results.get(4));
            case 6:
                return new Tuple6(results.get(0), results.get(1), results.get(2), results.get(3), results.get(4), results.get(5));
            case 7:
                return new Tuple7(results.get(0), results.get(1), results.get(2), results.get(3), results.get(4), results.get(5), results.get(6));
            case 8:
                return new Tuple8(results.get(0), results.get(1), results.get(2), results.get(3), results.get(4), results.get(5), results.get(6), results.get(7));
            case 9:
                return new Tuple9(results.get(0), results.get(1), results.get(2), results.get(3), results.get(4), results.get(5), results.get(6), results.get(7), results.get(8));
            case 10:
                return new Tuple10(results.get(0), results.get(1), results.get(2), results.get(3), results.get(4), results.get(5), results.get(6), results.get(7), results.get(8), results.get(9));
            default:
                return new Tuple2(results.get(0), results.get(1));
        }
    }

    public static class NFT extends StaticStruct {
        public Boolean isSupport;

        public BigInteger listingFee;

        public BigInteger auctionFee;

        public String nftTeam;

        public BigInteger percentNFTTeam;

        public NFT(Boolean isSupport, BigInteger listingFee, BigInteger auctionFee, String nftTeam, BigInteger percentNFTTeam) {
            super(new org.web3j.abi.datatypes.Bool(isSupport), new org.web3j.abi.datatypes.generated.Uint256(listingFee), new org.web3j.abi.datatypes.generated.Uint256(auctionFee), new org.web3j.abi.datatypes.Address(nftTeam), new org.web3j.abi.datatypes.generated.Uint256(percentNFTTeam));
            this.isSupport = isSupport;
            this.listingFee = listingFee;
            this.auctionFee = auctionFee;
            this.nftTeam = nftTeam;
            this.percentNFTTeam = percentNFTTeam;
        }

        public NFT(Bool isSupport, Uint256 listingFee, Uint256 auctionFee, Address nftTeam, Uint256 percentNFTTeam) {
            super(isSupport, listingFee, auctionFee, nftTeam, percentNFTTeam);
            this.isSupport = isSupport.getValue();
            this.listingFee = listingFee.getValue();
            this.auctionFee = auctionFee.getValue();
            this.nftTeam = nftTeam.getValue();
            this.percentNFTTeam = percentNFTTeam.getValue();
        }
    }

    public static class Auction extends StaticStruct {
        public Boolean isEnded;

        public String nft;

        public String winner;

        public String owner;

        public String currency;

        public BigInteger tokenId;

        public BigInteger fee;

        public BigInteger bid;

        public BigInteger bidDuration;

        public BigInteger end;

        public BigInteger id;

        public Auction(Boolean isEnded, String nft, String winner, String owner, String currency, BigInteger tokenId, BigInteger fee, BigInteger bid, BigInteger bidDuration, BigInteger end, BigInteger id) {
            super(new org.web3j.abi.datatypes.Bool(isEnded), new org.web3j.abi.datatypes.Address(nft), new org.web3j.abi.datatypes.Address(winner), new org.web3j.abi.datatypes.Address(owner), new org.web3j.abi.datatypes.Address(currency), new org.web3j.abi.datatypes.generated.Uint256(tokenId), new org.web3j.abi.datatypes.generated.Uint256(fee), new org.web3j.abi.datatypes.generated.Uint256(bid), new org.web3j.abi.datatypes.generated.Uint256(bidDuration), new org.web3j.abi.datatypes.generated.Uint256(end), new org.web3j.abi.datatypes.generated.Uint256(id));
            this.isEnded = isEnded;
            this.nft = nft;
            this.winner = winner;
            this.owner = owner;
            this.currency = currency;
            this.tokenId = tokenId;
            this.fee = fee;
            this.bid = bid;
            this.bidDuration = bidDuration;
            this.end = end;
            this.id = id;
        }

        public Auction(Bool isEnded, Address nft, Address winner, Address owner, Address currency, Uint256 tokenId, Uint256 fee, Uint256 bid, Uint256 bidDuration, Uint256 end, Uint256 id) {
            super(isEnded, nft, winner, owner, currency, tokenId, fee, bid, bidDuration, end, id);
            this.isEnded = isEnded.getValue();
            this.nft = nft.getValue();
            this.winner = winner.getValue();
            this.owner = owner.getValue();
            this.currency = currency.getValue();
            this.tokenId = tokenId.getValue();
            this.fee = fee.getValue();
            this.bid = bid.getValue();
            this.bidDuration = bidDuration.getValue();
            this.end = end.getValue();
            this.id = id.getValue();
        }
    }

    public static class Listing extends StaticStruct {
        public Boolean isEnded;

        public String nft;

        public String currency;

        public String owner;

        public BigInteger tokenId;

        public BigInteger fee;

        public BigInteger price;

        public BigInteger id;

        public Listing(Boolean isEnded, String nft, String currency, String owner, BigInteger tokenId, BigInteger fee, BigInteger price, BigInteger id) {
            super(new org.web3j.abi.datatypes.Bool(isEnded), new org.web3j.abi.datatypes.Address(nft), new org.web3j.abi.datatypes.Address(currency), new org.web3j.abi.datatypes.Address(owner), new org.web3j.abi.datatypes.generated.Uint256(tokenId), new org.web3j.abi.datatypes.generated.Uint256(fee), new org.web3j.abi.datatypes.generated.Uint256(price), new org.web3j.abi.datatypes.generated.Uint256(id));
            this.isEnded = isEnded;
            this.nft = nft;
            this.currency = currency;
            this.owner = owner;
            this.tokenId = tokenId;
            this.fee = fee;
            this.price = price;
            this.id = id;
        }

        public Listing(Bool isEnded, Address nft, Address currency, Address owner, Uint256 tokenId, Uint256 fee, Uint256 price, Uint256 id) {
            super(isEnded, nft, currency, owner, tokenId, fee, price, id);
            this.isEnded = isEnded.getValue();
            this.nft = nft.getValue();
            this.currency = currency.getValue();
            this.owner = owner.getValue();
            this.tokenId = tokenId.getValue();
            this.fee = fee.getValue();
            this.price = price.getValue();
            this.id = id.getValue();
        }
    }
}
