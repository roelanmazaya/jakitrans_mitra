package com.jakitrans.mc.utils.api.service;

import com.jakitrans.mc.json.ActivekatRequestJson;
import com.jakitrans.mc.json.AddEditItemRequestJson;
import com.jakitrans.mc.json.AddEditKategoriRequestJson;
import com.jakitrans.mc.json.BankResponseJson;
import com.jakitrans.mc.json.ChangePassRequestJson;
import com.jakitrans.mc.json.DetailRequestJson;
import com.jakitrans.mc.json.DetailTransResponseJson;
import com.jakitrans.mc.json.EditMerchantRequestJson;
import com.jakitrans.mc.json.EditProfileRequestJson;
import com.jakitrans.mc.json.FcmKeyResponse;
import com.jakitrans.mc.json.FcmResponse;
import com.jakitrans.mc.json.GetFiturResponseJson;
import com.jakitrans.mc.json.GetOnRequestJson;
import com.jakitrans.mc.json.GetOtpResponse;
import com.jakitrans.mc.json.HistoryRequestJson;
import com.jakitrans.mc.json.HistoryResponseJson;
import com.jakitrans.mc.json.HomeRequestJson;
import com.jakitrans.mc.json.HomeResponseJson;
import com.jakitrans.mc.json.ItemRequestJson;
import com.jakitrans.mc.json.ItemResponseJson;
import com.jakitrans.mc.json.KategoriRequestJson;
import com.jakitrans.mc.json.KategoriResponseJson;
import com.jakitrans.mc.json.LoginRequestJson;
import com.jakitrans.mc.json.LoginResponseJson;
import com.jakitrans.mc.json.MapKeyResponse;
import com.jakitrans.mc.json.PrivacyRequestJson;
import com.jakitrans.mc.json.PrivacyResponseJson;
import com.jakitrans.mc.json.RegisterRequestJson;
import com.jakitrans.mc.json.RegisterResponseJson;
import com.jakitrans.mc.json.ResponseJson;
import com.jakitrans.mc.json.SendFcmRequest;
import com.jakitrans.mc.json.TopupRequestJson;
import com.jakitrans.mc.json.TopupResponseJson;
import com.jakitrans.mc.json.VerifyCodeRequest;
import com.jakitrans.mc.json.VerifyCodeResponse;
import com.jakitrans.mc.json.WalletRequestJson;
import com.jakitrans.mc.json.WalletResponseJson;
import com.jakitrans.mc.json.WithdrawRequestJson;

import com.jakitrans.mc.json.transfer.GetHomeRequestJson;
import com.jakitrans.mc.json.transfer.GetReceiverRequestJson;
import com.jakitrans.mc.json.transfer.GetReceiverResponJson;
import com.jakitrans.mc.json.transfer.GetSaldoResponJson;
import com.jakitrans.mc.json.transfer.TransferRequestJson;
import com.jakitrans.mc.json.transfer.TransferResponJson;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

/**
 * Created by Ourdevelops Team on 10/13/2019.
 */

public interface MerchantService {

    @GET("merchant/kategorimerchant")
    Call<GetFiturResponseJson> getFitur();

    @POST("pelanggan/list_bank")
    Call<BankResponseJson> listbank(@Body WithdrawRequestJson param);

    @POST("merchant/kategorimerchantbyfitur")
    Call<GetFiturResponseJson> getKategori(@Body HistoryRequestJson param);

    @POST("merchant/onoff")
    Call<ResponseJson> turnon(@Body GetOnRequestJson param);

    @POST("merchant/login")
    Call<LoginResponseJson> login(@Body LoginRequestJson param);

    @POST("merchant/register_merchant")
    Call<RegisterResponseJson> register(@Body RegisterRequestJson param);

    @POST("merchant/forgot")
    Call<LoginResponseJson> forgot(@Body LoginRequestJson param);

    @POST("pelanggan/privacy")
    Call<PrivacyResponseJson> privacy(@Body PrivacyRequestJson param);

    @POST("merchant/edit_profile")
    Call<LoginResponseJson> editprofile(@Body EditProfileRequestJson param);

    @POST("merchant/edit_merchant")
    Call<LoginResponseJson> editmerchant(@Body EditMerchantRequestJson param);

    @POST("merchant/home")
    Call<HomeResponseJson> home(@Body HomeRequestJson param);

    @POST("merchant/history")
    Call<HistoryResponseJson> history(@Body HistoryRequestJson param);

    @POST("merchant/detail_transaksi")
    Call<DetailTransResponseJson> detailtrans(@Body DetailRequestJson param);

    @POST("merchant/kategori")
    Call<KategoriResponseJson> kategori(@Body KategoriRequestJson param);


    @POST("merchant/item")
    Call<ItemResponseJson> itemlist(@Body ItemRequestJson param);

    @POST("merchant/active_kategori")
    Call<ResponseJson> activekategori(@Body ActivekatRequestJson param);

    @POST("merchant/active_item")
    Call<ResponseJson> activeitem(@Body ActivekatRequestJson param);

    @POST("merchant/add_kategori")
    Call<ResponseJson> addkategori(@Body AddEditKategoriRequestJson param);

    @POST("merchant/edit_kategori")
    Call<ResponseJson> editkategori(@Body AddEditKategoriRequestJson param);

    @POST("merchant/delete_kategori")
    Call<ResponseJson> deletekategori(@Body AddEditKategoriRequestJson param);

    @POST("merchant/add_item")
    Call<ResponseJson> additem(@Body AddEditItemRequestJson param);

    @POST("merchant/edit_item")
    Call<ResponseJson> edititem(@Body AddEditItemRequestJson param);

    @POST("merchant/delete_item")
    Call<ResponseJson> deleteitem(@Body AddEditItemRequestJson param);

    @POST("pelanggan/topupstripe")
    Call<TopupResponseJson> topup(@Body TopupRequestJson param);

    @POST("merchant/withdraw")
    Call<ResponseJson> withdraw(@Body WithdrawRequestJson param);

    @POST("pelanggan/wallet")
    Call<WalletResponseJson> wallet(@Body WalletRequestJson param);

    @POST("merchant/topuppaypal")
    Call<ResponseJson> topuppaypal(@Body WithdrawRequestJson param);

    @POST("merchant/changepass")
    Call<LoginResponseJson> changepass(@Body ChangePassRequestJson param);

    @POST("driver/mwapikey")
    Call<MapKeyResponse> mwapikey();

    @POST("driver/mwapikey")
    Call<FcmKeyResponse> fcmapikey();

    @POST("pelanggan/device_notif")
    Call<FcmResponse> fcmnotif(@Body SendFcmRequest param);

    /*transfer*/
    @POST("payment/check_saldo")
    Call<GetSaldoResponJson> checksaldo(@Body GetHomeRequestJson param);

    @POST("payment/check_valid_trf")
    Call<GetReceiverResponJson> validation(@Body GetReceiverRequestJson param);

    @POST("payment/transfer_saldo")
    Call<TransferResponJson> transfer(@Body TransferRequestJson param);

    @GET("otp")
    Call<GetOtpResponse> getOtp(@HeaderMap Map<String, String> headers);

    @POST("otp")
    Call<VerifyCodeResponse> verifyOtp(@HeaderMap Map<String,
            String> headers, @Body VerifyCodeRequest param);
}
