# Chainverse SDK for Android

<img src="https://i.imgur.com/Dl4KbTt.png" width="100%" alt="NFT Shiba Inu">

Đơn giản hoá tích hợp Blokchain vào game của bạn với Chainverse SDK. 

Chainverse Native SDK sử dụng các API và tối ưu hóa dành riêng cho hệ điều hành để mang lại trải nghiệm người dùng tốt hơn. Chúng chứa chức năng cốt lõi để tích hợp vào game nhanh chóng hơn bao gồm các chức năng chính: Kết nối với ví Blockchain (TrustWallet và các ví khác) và trao đổi item NFT. 

## Mô hình Chainverse SDK
### Sequence flow 1
<img src="https://gblobscdn.gitbook.com/assets%2F-MfegUcnHBLzXgHaEQpA%2F-MfeiNnnfjqea_AfGmtY%2F-MfemVicwlXwEXbOG_HR%2Fcv1.jpg?alt=media&token=51652a27-807a-464d-bf0d-01d883c641b6" width="100%" alt="NFT Shiba Inu">

### Sequence flow 2
<img src="https://gblobscdn.gitbook.com/assets%2F-MfegUcnHBLzXgHaEQpA%2F-MfeiNnnfjqea_AfGmtY%2F-MfemkBnwJ-UhkunG7OT%2Fcv2.jpg?alt=media&token=7f403309-3062-479f-ac14-e6c0a1113c81" width="100%" alt="NFT Shiba Inu">

## Error Code
Danh sách mã lỗi của Chainverse SDK

| Name  | Error Code | Description | Suggestion | 
| ------------- | ------------- | ------------- | ------------- |
| ERROR_WAITING_INIT_SDK  | 1000  | Đang khởi tạo SDK | Hãy chờ cho đến khi khởi tạo xong | 
| ERROR_INITSDK  | 1001  | Lỗi khởi tạo SDK | Có thể có lỗi khi gọi lên blockchain | 
| ERROR_REQUEST_ITEM  | 1002  | Lỗi connect lấy về danh sách item NFT | Hãy thử lại | 
| ERROR_GAME_ADDRESS  | 1003  | Game address không đúng | Hãy kiểm tra lại | 
| ERROR_DEVELOPER_ADDRESS | 1004  | Developer address không đúng | Hãy kiểm tra lại |
| ERROR_GAME_PAUSE | 1005  | Game đang bị pause | Hãy kiểm tra lại |
| ERROR_DEVELOPER_PAUSE | 1006  | Developer đang bị pause | Hãy kiểm tra lại |

## Installation
### Cài đặt Chainverse SDK qua Gralde. 
#### Bước 1: Khai báo repository
Khai báo jitpack ở file build.gralde (project)

```
maven {
            url "https://jitpack.io"
        }
```
#### Bước 2: Khai báo dependencies

```
implementation 'com.github.gmogame:chainversesdk:alpha-1.0.15'
```

### Config trong file AndroidManifest.xml
#### Bước 1: Khai báo permission
Khai báo quyền sử dụng Internet. 
```
<uses-permission android:name="android.permission.INTERNET" />
```
#### Bước 2: Khai báo deeplink
Khai báo callback deeplink (trong tag activity) để ví Trust, Chainverse mở lại app (Khi connect ví).
{app_scheme} : Khai báo app scheme

```
<activity
            android:name=".MainActivity"
            android:label="@string/app_name"
            android:screenOrientation="portrait"
            android:launchMode="singleTask"
            android:theme="@style/Theme.AppCompat.NoActionBar">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>

            <intent-filter android:autoVerify="true">
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <action android:name="android.intent.action.VIEW" />
                <data android:scheme="{app_scheme}"/>
            </intent-filter>
        </activity>
```

#### Bước 3: Khai báo  ChainverseAcitivty
Để sử dụng giao diện của SDK 

```
<activity
            android:name="com.chainverse.sdk.ui.ChainverseSDKActivity"
            android:configChanges="orientation|keyboardHidden|screenSize|locale"
            android:theme="@style/ChainverseSDKTheme"
            android:windowSoftInputMode="adjustResize"/>
```

## Tích hợp SDK
### Trước khi bắt đầu
Bạn phải cài đặt Chainverse SDK (xem Hướng dẫn).

Tài liệu này chứa các tham số bắt buộc. Bạn phải đảm bảo khai báo chúng.

1. "Game Address": Địa chỉ contract của game.
2. "Developer Address": Địa chỉ contract của developer.
3. "App Scheme": Khai báo scheme để connect Trust Wallet, Chainverse.

### Khởi tạo Chainverse SDK
#### Bước 1: Import dependencies 
Import class ChainverseSDK  và ChainverseCallback
```
import com.chainverse.sdk.ChainverseCallback;
import com.chainverse.sdk.ChainverseSDK;
```

#### Bước 2: Khởi tạo SDK
Khai báo DeveloperAddress, GameAddress, Scheme
```
String developerAddress = "DeveloperAddress";
String gameAddress = "GameAddress";
ChainverseSDK.getInstance().init(developerAddress,gameAddress, activity, new ChainverseCallback);
ChainverseSDK.getInstance().setScheme("trust-rn-example1://");
```

#### Bước 3: Implement các hàm callback
##### 1. Callback onInitSDKSuccess
Khi khởi tạo SDK callback sẽ được gửi lại, để thông báo là đã khởi tạo thành công.

Lưu ý: Các chức năng trong SDK sẽ không được thực thi, nếu quá trình khởi tạo SDK bị lỗi. Và không có callback onInitSDKSuccess. Mã lỗi sẽ được callback ở hàm onError.

```
@Override
public void onInitSDKSuccess() {
}
```

##### 2. Callback onError
Khi khởi tạo SDK hoặc có bất kỳ lỗi nào xả ra sẽ có callback này. Thông tin trả về là mã lỗi. Bạn có thể xem tất cả mã lỗi ở trang Error  Codes .

```
@Override
public void onError(int error) {
     switch (error){
         case ChainverseError.ERROR_INIT_SDK:
              break;
         }

     }

```

##### 3. Callback onConnectSuccess
Khi user connect tới ví Trust, Chainverse thành công thì sẽ có callback này. Thông tin trả về là địa chỉ ví của user. 

```
@Override
public void onConnectSuccess(String address) {
   
}
```

##### 4. Callback onLogout
Khi user thực hiện thao tác đăng xuất callback này sẽ được gọi. Thông tin trả về là địa chỉ ví của user. 

```
@Override
public void onLogout(String address) {
            
}
```

##### 5. Callback onGetItems
Khi hàm `ChainverseSDK.getInstance().getItems;` callback này sẽ trả về thông tin là danh sách ITEM của user đó. Và khi​ chuyển Item NFT qua lại giữa user - user trong 1 game, và chuyển từ game này sang game kia. Callback này sẽ được gọi REALTIME. 

Bạn sẽ xử lý ITEM trong game của bạn ở callback này.

```
@Override
public void onGetItems(ArrayList<ChainverseItem> items) {
            
}
```

##### 6. Callback onItemUpdate
Khi​ chuyển Item NFT qua lại giữa user - user trong 1 game, và chuyển từ game này sang game kia. Callback này sẽ được gọi REALTIME. Thông tin trả về là 01 ITEM đã move. 

Bạn sẽ xử lý ITEM trong game của bạn ở callback này.

```
@Override
public void onItemUpdate(ChainverseItem item, int type) {
    switch (type){
       case ChainverseItem.TRANSFER_ITEM_TO_USER:
            //Xử lý item trong game khi item NFT chuyển tới tài khoản của bạn
            break;
       case ChainverseItem.TRANSFER_ITEM_FROM_USER:
            //Xử lý item trong game khi item NFT của bạn chuyến tời tài khoản khác
            break;
    }
}
```

##### Full example


```
 String developerAddress = "0xE1717d89f2d7A7b4834c2724408b319ABAf500ec";
 String gameAddress = "0xD146b45817fd18555c59c061C840e3a446Cd5A6c";
 ChainverseSDK.getInstance().init(developerAddress,gameAddress,this, new ChainverseCallback() {

            @Override
            public void onInitSDKSuccess() {

            }

            @Override
            public void onError(int error) {
                switch (error){
                    case ChainverseError.ERROR_INIT_SDK:
                        break;
                }

            }

            @Override
            public void onItemUpdate(ChainverseItem item, int type) {
                LogUtil.log("onItemUpdate",item);
                switch (type){
                    case ChainverseItem.TRANSFER_ITEM_TO_USER:
                        break;
                    case ChainverseItem.TRANSFER_ITEM_FROM_USER:
                        break;
                }
            }

            @Override
            public void onGetItems(ArrayList<ChainverseItem> items) {
                LogUtil.log("onGetItems",items);
            }


            @Override
            public void onConnectSuccess(String address) {
                ChainverseSDK.getInstance().getItems();
            }

            @Override
            public void onLogout(String address) {
                
            }
        });
        ChainverseSDK.getInstance().setScheme("trust-rn-example1://");
```

## Functions
### Các hàm API
#### 1. Hàm showConnectView
Hàm này hiển thị màn hình danh sách các ví để user lựa chọn connect. 

```
ChainverseSDK.getInstance().showConnectView();
```

#### 2. Hàm connectWithTrust
Sử dụng hàm này để kết nối với ví Trust, mà không cần hiển thị giao diện. 

```
ChainverseSDK.getInstance().connectWithTrust();
```

#### 3. Hàm connectWithChainverse
Sử dụng hàm này để kết nối với ví Chainverse, mà không cần hiển thị giao diện. 

```
ChainverseSDK.getInstance().connectWithChainverse();
```

#### 4. Hàm getItems
Sử dụng hàm này để lấy danh sách ITEM của user. Thông tin sẽ được trả về qua callback  onGetItems .

```
ChainverseSDK.getInstance().getItems();

//Callback
@Override
public void onGetItems(ArrayList<ChainverseItem> items) {
            
}
```

#### 5. Hàm logout
Gọi hàm này để thực hiện logout. Thông tin được trả về qua callback onLogout .

```
ChainverseSDK.getInstance().logout();

//Callback
@Override
public void onLogout(String address) {
            
}
```

#### 6. Hàm hứng data được trả về từ ví Trust, Chainverse
Khi connect thành công với ví Trust. Trust sẽ mở lại app/game thông qua scheme (đã khai báo ở phần Intergrate SDK). Vì vậy cần khai báo các hàm này để Chainverse SDK xử lý dữ liệu được trả về từ ví Trust.

```
ChainverseSDK.getInstance().onNewIntent(intent);
```

Khai báo trong hàm onNewIntent của Activity

```
@Override
protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    // getIntent() should always return the most recent
    setIntent(intent);
    ChainverseSDK.getInstance().onNewIntent(intent);
}
```

#### 7. Hàm setKeepConnect
Hàm này tuỳ chọn thiết lập trạng thái giữ connect với ví Trust (Khi vào lại app không cần phải kết nối lại ví)
 
true : Giữ trạng thái keep connect.
false: Không giữ trạng thái keep connect.

```
ChainverseSDK.getInstance().setKeepConnect(true);
```

#### 8. Hàm getVersion
Trả về version của SDK

```
ChainverseSDK.getInstance().getVersion();
```

#### 9. Hàm setScheme
Để config khi connect với ví Trust (Ví Trust/Chainverse sẽ mở lại app thông qua config này) 

```
ChainverseSDK.getInstance().setScheme("trust-rn-example1://");
```

#### 10. Hàm getUser
Trả về thông tin của user bao gồm : address và signature

```
ChainverseUser info = ChainverseSDK.getInstance().getUser();
info.getAddress();
info.getSignature();
```

#### 11. Hàm isUserConnected
Kiểm tra trạng thái connect ví của user. Trả về boolean

```
boolean isConnect = ChainverseSDK.getInstance().isUserConnected()
```


## License

Chainverse SDK Android sử dụng những thư viện sau:
###### 1. Retrofit
Home page: https://square.github.io/retrofit/
Mục đích sử dụng: Để kết nối REST (API)
###### 2. RxJava - RxAndroid
Home page: https://github.com/ReactiveX/RxJava
Mục đích sử dụng: Xử lý bất động bộ khi connect API
###### 3. Socket io
Home page: https://socket.io/
Mục đích sử dụng: Xử lý realtime
###### 4. Web3j
Home page: https://github.com/web3j/web3j
Mục đích sử dụng: Connect với blockchain
