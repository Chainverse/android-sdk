package com.chainverse.sdk.blockchain;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.4.1.
 */
@SuppressWarnings("rawtypes")
public class GameService extends Contract {
    public static final String BINARY = "0x60806040523480156200001157600080fd5b506040516200146f3803806200146f833981016040819052620000349162000280565b825162000049906003906020860190620001bd565b50600480546001600160a01b038085166001600160a01b0319928316179092556005805492841692909116919091179055620000956000805160206200144f83398151915280620000b9565b620000b06000805160206200144f833981519152336200010d565b505050620003d0565b600082815260208190526040902060010154819060405184907fbd79b86ffe0ab8e8776151514217cd7cacd52c909f66475c3af44e129f0b00ff90600090a460009182526020829052604090912060010155565b6200011982826200011d565b5050565b6000828152602081815260408083206001600160a01b038516845290915290205460ff1662000119576000828152602081815260408083206001600160a01b03851684529091529020805460ff19166001179055620001793390565b6001600160a01b0316816001600160a01b0316837f2f8788117e7eff1d82e926ec794901d17c78024a50270940304540a733656f0d60405160405180910390a45050565b828054620001cb906200037d565b90600052602060002090601f016020900481019282620001ef57600085556200023a565b82601f106200020a57805160ff19168380011785556200023a565b828001600101855582156200023a579182015b828111156200023a5782518255916020019190600101906200021d565b50620002489291506200024c565b5090565b5b808211156200024857600081556001016200024d565b80516001600160a01b03811681146200027b57600080fd5b919050565b60008060006060848603121562000295578283fd5b83516001600160401b0380821115620002ac578485fd5b818601915086601f830112620002c0578485fd5b815181811115620002d557620002d5620003ba565b604051601f8201601f19908116603f01168101908382118183101715620003005762000300620003ba565b816040528281526020935089848487010111156200031c578788fd5b8791505b828210156200033f578482018401518183018501529083019062000320565b828211156200035057878484830101525b96506200036291505086820162000263565b93505050620003746040850162000263565b90509250925092565b600181811c908216806200039257607f821691505b60208210811415620003b457634e487b7160e01b600052602260045260246000fd5b50919050565b634e487b7160e01b600052604160045260246000fd5b61106f80620003e06000396000f3fe608060405234801561001057600080fd5b50600436106100f55760003560e01c806362a1a96a11610097578063a217fddf11610066578063a217fddf1461025b578063d547741f14610263578063e58378bb14610276578063eb12d61e1461029d57600080fd5b806362a1a96a146101ca5780637ecebe00146101dd57806391d14854146101fd578063a09b8c3d1461023457600080fd5b806320606b70116100d357806320606b701461014a578063248a9ca31461017f5780632f2ff15d146101a257806336568abe146101b757600080fd5b806301ffc9a7146100fa57806306fdde03146101225780630e316ab714610137575b600080fd5b61010d610108366004610e3f565b6102b0565b60405190151581526020015b60405180910390f35b61012a6102e7565b6040516101199190610f4b565b61010d610145366004610de2565b610379565b6101717f8cad95687ba82c2ce50e74f7b754645e5117c3a5bec8151c0726d5857980a86681565b604051908152602001610119565b61017161018d366004610dfc565b60009081526020819052604090206001015490565b6101b56101b0366004610e14565b610479565b005b6101b56101c5366004610e14565b6104a4565b6101b56101d8366004610e67565b610530565b6101716101eb366004610de2565b60026020526000908152604090205481565b61010d61020b366004610e14565b6000918252602082815260408084206001600160a01b0393909316845291905290205460ff1690565b6101717f754fb7da24e4718656e304abcda4d125d4b8b9a60a8c9b7cfe812030e90ffe5c81565b610171600081565b6101b5610271366004610e14565b6106b7565b6101717fb19546dff01e856fb3f010c267a7b1c60363cf8a4664e21cc89c26224620214e81565b61010d6102ab366004610de2565b6106dd565b60006001600160e01b03198216637965db0b60e01b14806102e157506301ffc9a760e01b6001600160e01b03198316145b92915050565b6060600380546102f690610ffc565b80601f016020809104026020016040519081016040528092919081815260200182805461032290610ffc565b801561036f5780601f106103445761010080835404028352916020019161036f565b820191906000526020600020905b81548152906001019060200180831161035257829003601f168201915b5050505050905090565b60007fb19546dff01e856fb3f010c267a7b1c60363cf8a4664e21cc89c26224620214e6103a681336107c8565b6001600160a01b03831660009081526001602081905260409091205460ff161515146104195760405162461bcd60e51b815260206004820152601660248201527f47616d653a205369676e6572206973206578697374730000000000000000000060448201526064015b60405180910390fd5b6001600160a01b038316600081815260016020908152604091829020805460ff1916905590519182527f1803740ef72fc16e647c10fe2d31cf61a1578081960c2e3fb7f5aa957e82f55091015b60405180910390a1600191505b50919050565b60008281526020819052604090206001015461049581336107c8565b61049f8383610846565b505050565b6001600160a01b03811633146105225760405162461bcd60e51b815260206004820152602f60248201527f416363657373436f6e74726f6c3a2063616e206f6e6c792072656e6f756e636560448201527f20726f6c657320666f722073656c6600000000000000000000000000000000006064820152608401610410565b61052c82826108e4565b5050565b60008086156105b7576004546001600160a01b03166105b75760405162461bcd60e51b815260206004820152602560248201527f47616d653a20476f7665726e616e6365546f6b656e206973206e6f742072656760448201527f69737465640000000000000000000000000000000000000000000000000000006064820152608401610410565b8515610620576005546001600160a01b03166106205760405162461bcd60e51b815260206004820152602260248201527f47616d653a205574696c697479546f6b656e206973206e6f7420726567697374604482015261195960f21b6064820152608401610410565b604080517f754fb7da24e4718656e304abcda4d125d4b8b9a60a8c9b7cfe812030e90ffe5c60208201529081018a9052606081018990526080810188905260a0810187905260009060c0016040516020818303038152906040528051906020012090506106918a8a83898989610963565b6106ab57634e487b7160e01b600052600160045260246000fd5b50505050505050505050565b6000828152602081905260409020600101546106d381336107c8565b61049f83836108e4565b60007fb19546dff01e856fb3f010c267a7b1c60363cf8a4664e21cc89c26224620214e61070a81336107c8565b6001600160a01b03831660009081526001602052604090205460ff16156107735760405162461bcd60e51b815260206004820152601660248201527f47616d653a205369676e657220697320657869737473000000000000000000006044820152606401610410565b6001600160a01b038316600081815260016020818152604092839020805460ff191690921790915590519182527f637c77a2d598a51d085d4a2413332c45a235a25ee855bf3dfcdc2c8fcf02860f9101610466565b6000828152602081815260408083206001600160a01b038516845290915290205460ff1661052c57610804816001600160a01b03166014610bd0565b61080f836020610bd0565b604051602001610820929190610eca565b60408051601f198184030181529082905262461bcd60e51b825261041091600401610f4b565b6000828152602081815260408083206001600160a01b038516845290915290205460ff1661052c576000828152602081815260408083206001600160a01b03851684529091529020805460ff191660011790556108a03390565b6001600160a01b0316816001600160a01b0316837f2f8788117e7eff1d82e926ec794901d17c78024a50270940304540a733656f0d60405160405180910390a45050565b6000828152602081815260408083206001600160a01b038516845290915290205460ff161561052c576000828152602081815260408083206001600160a01b0385168085529252808320805460ff1916905551339285917ff6391f5c32d9c69d2a47ea670b442974b53935d1edc7fd64eb21e047a839171b9190a45050565b6000807f8cad95687ba82c2ce50e74f7b754645e5117c3a5bec8151c0726d5857980a86661098f6102e7565b8051906020012061099d4690565b604080516020810194909452830191909152606082015230608082015260a00160405160208183030381529060405280519060200120905060008187604051602001610a0092919061190160f01b81526002810192909252602282015260420190565b60408051601f198184030181528282528051602091820120600080855291840180845281905260ff8a169284019290925260608301889052608083018790529092509060019060a0016020604051602081039080840390855afa158015610a6b573d6000803e3d6000fd5b5050604051601f1901519150506001600160a01b03811615801590610aae57506001600160a01b03811660009081526001602081905260409091205460ff161515145b610afa5760405162461bcd60e51b815260206004820152601760248201527f47616d653a20696e76616c6964207369676e61747572650000000000000000006044820152606401610410565b6001600160a01b0381166000908152600260205260408120805491610b1e83611031565b919050558a14610b705760405162461bcd60e51b815260206004820152601360248201527f47616d653a20696e76616c6964206e6f6e6365000000000000000000000000006044820152606401610410565b88421115610bc05760405162461bcd60e51b815260206004820152601760248201527f47616d653a207369676e617475726520657870697265640000000000000000006044820152606401610410565b5060019998505050505050505050565b60606000610bdf836002610f96565b610bea906002610f7e565b67ffffffffffffffff811115610c1057634e487b7160e01b600052604160045260246000fd5b6040519080825280601f01601f191660200182016040528015610c3a576020820181803683370190505b509050600360fc1b81600081518110610c6357634e487b7160e01b600052603260045260246000fd5b60200101906001600160f81b031916908160001a905350600f60fb1b81600181518110610ca057634e487b7160e01b600052603260045260246000fd5b60200101906001600160f81b031916908160001a9053506000610cc4846002610f96565b610ccf906001610f7e565b90505b6001811115610d70577f303132333435363738396162636465660000000000000000000000000000000085600f1660108110610d1e57634e487b7160e01b600052603260045260246000fd5b1a60f81b828281518110610d4257634e487b7160e01b600052603260045260246000fd5b60200101906001600160f81b031916908160001a90535060049490941c93610d6981610fe5565b9050610cd2565b508315610dbf5760405162461bcd60e51b815260206004820181905260248201527f537472696e67733a20686578206c656e67746820696e73756666696369656e746044820152606401610410565b9392505050565b80356001600160a01b0381168114610ddd57600080fd5b919050565b600060208284031215610df3578081fd5b610dbf82610dc6565b600060208284031215610e0d578081fd5b5035919050565b60008060408385031215610e26578081fd5b82359150610e3660208401610dc6565b90509250929050565b600060208284031215610e50578081fd5b81356001600160e01b031981168114610dbf578182fd5b600080600080600080600060e0888a031215610e81578283fd5b87359650602088013595506040880135945060608801359350608088013560ff81168114610ead578384fd5b9699959850939692959460a0840135945060c09093013592915050565b7f416363657373436f6e74726f6c3a206163636f756e7420000000000000000000815260008351610f02816017850160208801610fb5565b7f206973206d697373696e6720726f6c65200000000000000000000000000000006017918401918201528351610f3f816028840160208801610fb5565b01602801949350505050565b6020815260008251806020840152610f6a816040850160208701610fb5565b601f01601f19169190910160400192915050565b60008219821115610f9157610f9161104c565b500190565b6000816000190483118215151615610fb057610fb061104c565b500290565b60005b83811015610fd0578181015183820152602001610fb8565b83811115610fdf576000848401525b50505050565b600081610ff457610ff461104c565b506000190190565b600181811c9082168061101057607f821691505b6020821081141561047357634e487b7160e01b600052602260045260246000fd5b60006000198214156110455761104561104c565b5060010190565b634e487b7160e01b600052601160045260246000fdfea164736f6c6343000804000ab19546dff01e856fb3f010c267a7b1c60363cf8a4664e21cc89c26224620214e";

    public static final String FUNC_DEFAULT_ADMIN_ROLE = "DEFAULT_ADMIN_ROLE";

    public static final String FUNC_DOMAIN_TYPEHASH = "DOMAIN_TYPEHASH";

    public static final String FUNC_OWNER_ROLE = "OWNER_ROLE";

    public static final String FUNC_USER_USE_SERVICE_TYPEHASH = "USER_USE_SERVICE_TYPEHASH";

    public static final String FUNC_ADDSIGNER = "addSigner";

    public static final String FUNC_GETROLEADMIN = "getRoleAdmin";

    public static final String FUNC_GRANTROLE = "grantRole";

    public static final String FUNC_HASROLE = "hasRole";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_NONCES = "nonces";

    public static final String FUNC_REMOVESIGNER = "removeSigner";

    public static final String FUNC_RENOUNCEROLE = "renounceRole";

    public static final String FUNC_REVOKEROLE = "revokeRole";

    public static final String FUNC_SUPPORTSINTERFACE = "supportsInterface";

    public static final String FUNC_USESERVICE = "useService";

    public static final Event ADDSIGNER_EVENT = new Event("AddSigner", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
    ;

    public static final Event REMOVESIGNER_EVENT = new Event("RemoveSigner", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
    ;

    public static final Event ROLEADMINCHANGED_EVENT = new Event("RoleAdminChanged", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>(true) {}, new TypeReference<Bytes32>(true) {}, new TypeReference<Bytes32>(true) {}));
    ;

    public static final Event ROLEGRANTED_EVENT = new Event("RoleGranted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event ROLEREVOKED_EVENT = new Event("RoleRevoked", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    @Deprecated
    protected GameService(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected GameService(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected GameService(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected GameService(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<AddSignerEventResponse> getAddSignerEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ADDSIGNER_EVENT, transactionReceipt);
        ArrayList<AddSignerEventResponse> responses = new ArrayList<AddSignerEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            AddSignerEventResponse typedResponse = new AddSignerEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.signer = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<AddSignerEventResponse> addSignerEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, AddSignerEventResponse>() {
            @Override
            public AddSignerEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ADDSIGNER_EVENT, log);
                AddSignerEventResponse typedResponse = new AddSignerEventResponse();
                typedResponse.log = log;
                typedResponse.signer = (String) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<AddSignerEventResponse> addSignerEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ADDSIGNER_EVENT));
        return addSignerEventFlowable(filter);
    }

    public List<RemoveSignerEventResponse> getRemoveSignerEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(REMOVESIGNER_EVENT, transactionReceipt);
        ArrayList<RemoveSignerEventResponse> responses = new ArrayList<RemoveSignerEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RemoveSignerEventResponse typedResponse = new RemoveSignerEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.signer = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<RemoveSignerEventResponse> removeSignerEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, RemoveSignerEventResponse>() {
            @Override
            public RemoveSignerEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(REMOVESIGNER_EVENT, log);
                RemoveSignerEventResponse typedResponse = new RemoveSignerEventResponse();
                typedResponse.log = log;
                typedResponse.signer = (String) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<RemoveSignerEventResponse> removeSignerEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(REMOVESIGNER_EVENT));
        return removeSignerEventFlowable(filter);
    }

    public List<RoleAdminChangedEventResponse> getRoleAdminChangedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ROLEADMINCHANGED_EVENT, transactionReceipt);
        ArrayList<RoleAdminChangedEventResponse> responses = new ArrayList<RoleAdminChangedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RoleAdminChangedEventResponse typedResponse = new RoleAdminChangedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.role = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.previousAdminRole = (byte[]) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.newAdminRole = (byte[]) eventValues.getIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<RoleAdminChangedEventResponse> roleAdminChangedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, RoleAdminChangedEventResponse>() {
            @Override
            public RoleAdminChangedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ROLEADMINCHANGED_EVENT, log);
                RoleAdminChangedEventResponse typedResponse = new RoleAdminChangedEventResponse();
                typedResponse.log = log;
                typedResponse.role = (byte[]) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.previousAdminRole = (byte[]) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.newAdminRole = (byte[]) eventValues.getIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<RoleAdminChangedEventResponse> roleAdminChangedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ROLEADMINCHANGED_EVENT));
        return roleAdminChangedEventFlowable(filter);
    }

    public List<RoleGrantedEventResponse> getRoleGrantedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ROLEGRANTED_EVENT, transactionReceipt);
        ArrayList<RoleGrantedEventResponse> responses = new ArrayList<RoleGrantedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RoleGrantedEventResponse typedResponse = new RoleGrantedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.role = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.account = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.sender = (String) eventValues.getIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<RoleGrantedEventResponse> roleGrantedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, RoleGrantedEventResponse>() {
            @Override
            public RoleGrantedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ROLEGRANTED_EVENT, log);
                RoleGrantedEventResponse typedResponse = new RoleGrantedEventResponse();
                typedResponse.log = log;
                typedResponse.role = (byte[]) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.account = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.sender = (String) eventValues.getIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<RoleGrantedEventResponse> roleGrantedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ROLEGRANTED_EVENT));
        return roleGrantedEventFlowable(filter);
    }

    public List<RoleRevokedEventResponse> getRoleRevokedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ROLEREVOKED_EVENT, transactionReceipt);
        ArrayList<RoleRevokedEventResponse> responses = new ArrayList<RoleRevokedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RoleRevokedEventResponse typedResponse = new RoleRevokedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.role = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.account = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.sender = (String) eventValues.getIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<RoleRevokedEventResponse> roleRevokedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, RoleRevokedEventResponse>() {
            @Override
            public RoleRevokedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ROLEREVOKED_EVENT, log);
                RoleRevokedEventResponse typedResponse = new RoleRevokedEventResponse();
                typedResponse.log = log;
                typedResponse.role = (byte[]) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.account = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.sender = (String) eventValues.getIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<RoleRevokedEventResponse> roleRevokedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ROLEREVOKED_EVENT));
        return roleRevokedEventFlowable(filter);
    }

    public RemoteFunctionCall<byte[]> DEFAULT_ADMIN_ROLE() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_DEFAULT_ADMIN_ROLE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<byte[]> DOMAIN_TYPEHASH() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_DOMAIN_TYPEHASH, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<byte[]> OWNER_ROLE() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_OWNER_ROLE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<byte[]> USER_USE_SERVICE_TYPEHASH() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_USER_USE_SERVICE_TYPEHASH, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<TransactionReceipt> addSigner(String signer) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ADDSIGNER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, signer)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<byte[]> getRoleAdmin(byte[] role) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETROLEADMIN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(role)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<TransactionReceipt> grantRole(byte[] role, String account) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_GRANTROLE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(role), 
                new org.web3j.abi.datatypes.Address(160, account)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> hasRole(byte[] role, String account) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_HASROLE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(role), 
                new org.web3j.abi.datatypes.Address(160, account)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<String> name() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_NAME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> nonces(String param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_NONCES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> removeSigner(String signer) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_REMOVESIGNER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, signer)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> renounceRole(byte[] role, String account) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_RENOUNCEROLE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(role), 
                new org.web3j.abi.datatypes.Address(160, account)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> revokeRole(byte[] role, String account) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_REVOKEROLE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(role), 
                new org.web3j.abi.datatypes.Address(160, account)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> supportsInterface(byte[] interfaceId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_SUPPORTSINTERFACE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes4(interfaceId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> useService(BigInteger nonce, BigInteger expiry, BigInteger govToken, BigInteger utilToken, BigInteger v, byte[] r, byte[] s) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_USESERVICE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(nonce), 
                new org.web3j.abi.datatypes.generated.Uint256(expiry), 
                new org.web3j.abi.datatypes.generated.Uint256(govToken), 
                new org.web3j.abi.datatypes.generated.Uint256(utilToken), 
                new org.web3j.abi.datatypes.generated.Uint8(v), 
                new org.web3j.abi.datatypes.generated.Bytes32(r), 
                new org.web3j.abi.datatypes.generated.Bytes32(s)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static GameService load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new GameService(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static GameService load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new GameService(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static GameService load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new GameService(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static GameService load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new GameService(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<GameService> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, String serviceName, String govToken, String utilToken) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(serviceName), 
                new org.web3j.abi.datatypes.Address(160, govToken), 
                new org.web3j.abi.datatypes.Address(160, utilToken)));
        return deployRemoteCall(GameService.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<GameService> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, String serviceName, String govToken, String utilToken) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(serviceName), 
                new org.web3j.abi.datatypes.Address(160, govToken), 
                new org.web3j.abi.datatypes.Address(160, utilToken)));
        return deployRemoteCall(GameService.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<GameService> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String serviceName, String govToken, String utilToken) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(serviceName), 
                new org.web3j.abi.datatypes.Address(160, govToken), 
                new org.web3j.abi.datatypes.Address(160, utilToken)));
        return deployRemoteCall(GameService.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<GameService> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String serviceName, String govToken, String utilToken) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(serviceName), 
                new org.web3j.abi.datatypes.Address(160, govToken), 
                new org.web3j.abi.datatypes.Address(160, utilToken)));
        return deployRemoteCall(GameService.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static class AddSignerEventResponse extends BaseEventResponse {
        public String signer;
    }

    public static class RemoveSignerEventResponse extends BaseEventResponse {
        public String signer;
    }

    public static class RoleAdminChangedEventResponse extends BaseEventResponse {
        public byte[] role;

        public byte[] previousAdminRole;

        public byte[] newAdminRole;
    }

    public static class RoleGrantedEventResponse extends BaseEventResponse {
        public byte[] role;

        public String account;

        public String sender;
    }

    public static class RoleRevokedEventResponse extends BaseEventResponse {
        public byte[] role;

        public String account;

        public String sender;
    }
}
