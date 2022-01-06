package com.chainverse.sdk.blockchain;

import com.chainverse.sdk.ChainverseError;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.AbiTypes;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.StaticArray;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.StructType;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.Tuple;
import org.web3j.tuples.generated.Tuple1;
import org.web3j.tuples.generated.Tuple10;
import org.web3j.tuples.generated.Tuple16;
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

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Arrays;
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
                        break;
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
                if (abi.getString("stateMutability").equals(NONPAYABLE)) {
                    output = nonpayable(abi.getString("name"), inputParams, outputParams);
                }
                if (abi.getString("stateMutability").equals(VIEW) && abi.has("outputs")) {
//                    if (abi.getJSONArray("outputs").length() == 1) {
//                        output = view(abi.getString("name"), inputParams, outputParams);
//                    } else {
//                        output = executeCallMultipleValueTuple(abi.getString("name"), inputParams, outputParams);
//                    }
                    output = excuteCallMulti(abi.getString("name"), inputParams);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return output;
    }

    protected RemoteFunctionCall nonpayable(String func, List inputs, List output) {
        final Function function = new Function(func, inputs, output);
        return executeRemoteCallTransaction(function);
    }

    protected RemoteFunctionCall view(String func, List inputs, List output) {
        TypeReference type = (TypeReference) output.get(0);
        final Function function = new Function(func, inputs, output);
        return executeRemoteCallSingleValueReturn(function, getClassType(type));
    }

    protected RemoteFunctionCall executeCallMultipleValueTuple(String func, List inputs, List outputs) {
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

    protected RemoteFunctionCall excuteCallMulti(String func, List inputs) {

        System.out.println("run herere" + func);
        List<List<TypeReference<?>>> outputParameters = new ArrayList();

        List auction = new ArrayList();
        auction.add(new TypeReference<Bool>() {
        });
        auction.add(new TypeReference<Address>() {
        });
        auction.add(new TypeReference<Address>() {
        });
        auction.add(new TypeReference<Uint256>() {
        });

        outputParameters.add(auction);

        final Function function = new Function(func, inputs, auction);
        return new RemoteFunctionCall(function,
                new Callable() {
                    @Override
                    public Tuple2 call() throws Exception {
                        List results = executeCallMultipleValueReturn(function);
                        return new Tuple2(
                                results.get(0),
                                results.get(1));
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
                return new Tuple2(results.get(0).getValue(), results.get(1).getValue());
            case 3:
                return new Tuple3(results.get(0).getValue(), results.get(1).getValue(), results.get(2).getValue());
            case 4:
                return new Tuple4(results.get(0).getValue(), results.get(1).getValue(), results.get(2).getValue(), results.get(3).getValue());
            case 5:
                return new Tuple5(results.get(0).getValue(), results.get(1).getValue(), results.get(2).getValue(), results.get(3).getValue(), results.get(4).getValue());
            case 6:
                return new Tuple6(results.get(0).getValue(), results.get(1).getValue(), results.get(2).getValue(), results.get(3).getValue(), results.get(4).getValue(), results.get(5).getValue());
            case 7:
                return new Tuple7(results.get(0).getValue(), results.get(1).getValue(), results.get(2).getValue(), results.get(3).getValue(), results.get(4).getValue(), results.get(5).getValue(), results.get(6).getValue());
            case 8:
                return new Tuple8(results.get(0).getValue(), results.get(1).getValue(), results.get(2).getValue(), results.get(3).getValue(), results.get(4).getValue(), results.get(5).getValue(), results.get(6).getValue(), results.get(7).getValue());
            case 9:
                return new Tuple9(results.get(0).getValue(), results.get(1).getValue(), results.get(2).getValue(), results.get(3).getValue(), results.get(4).getValue(), results.get(5).getValue(), results.get(6).getValue(), results.get(7).getValue(), results.get(8).getValue());
            case 10:
                return new Tuple10(results.get(0).getValue(), results.get(1).getValue(), results.get(2).getValue(), results.get(3).getValue(), results.get(4).getValue(), results.get(5).getValue(), results.get(6).getValue(), results.get(7).getValue(), results.get(8).getValue(), results.get(9).getValue());
            default:
                return new Tuple2(results.get(0).getValue(), results.get(1).getValue());
        }
    }

    class StaticStruct extends StaticArray<Type> implements StructType {

        private final List<Class<Type>> itemTypes = new ArrayList<>();

        @SuppressWarnings("unchecked")
        public StaticStruct(List<Type> values) {
            super(Type.class, values.size(), values);
            for (Type value : values) {
                itemTypes.add((Class<Type>) value.getClass());
            }
        }

        @SafeVarargs
        public StaticStruct(Type... values) {
            this(Arrays.asList(values));
        }

        @Override
        public String getTypeAsString() {
            final StringBuilder type = new StringBuilder("(");
            for (int i = 0; i < itemTypes.size(); ++i) {
                final Class<Type> cls = itemTypes.get(i);
                if (StructType.class.isAssignableFrom(cls)) {
                    type.append(getValue().get(i).getTypeAsString());
                } else {
                    type.append(AbiTypes.getTypeAString(cls));
                }
                if (i < itemTypes.size() - 1) {
                    type.append(",");
                }
            }
            type.append(")");
            return type.toString();
        }
    }
}
