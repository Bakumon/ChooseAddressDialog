package io.github.bakumon.chooseaddressdialog.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.github.bakumon.chooseaddressdialog.R;


/**
 * 三级联动选择地区
 * 有对应的code
 * 记住上次选择的值
 * Created by bakumon on 2016/10/19.
 */
public class ChooseAddressDialog extends Dialog implements NumberPicker.OnValueChangeListener, View.OnClickListener {

    private NumberPicker mNpProvinces;//省
    private NumberPicker mNpCities;//市
    private NumberPicker mNpDistricts;//区
    private Button mBtnAffirm; // 确认按钮

    private MProvince mCurrentMProvince;
    private MCity mCurrentMCity;
    private MDistrict mCurrentMDistrict;

    private String mInitProvinceCode; // 初始省
    private String mInitCityCode; // 初始市
    private String mInitDistrictCode; // 初始区

    private String mInitProvinceName; // 初始省
    private String mInitCityName; // 初始市
    private String mInitDistrictName; // 初始区

    private boolean mInitIsByName; // 是否是用 name 回显 true：name false：code
    private Context mContext;

    /**
     * 初始化，不回显省市区
     *
     * @param context 上下文
     */
    public ChooseAddressDialog(Context context) {
        this(context, null, null, null, false);
    }

    /**
     * 仅使用 code 初始化
     *
     * @param context          上下文
     * @param initProvinceCode 省 code
     * @param initCityCode     市 code
     * @param initDistrictCode 区 code
     */
    public ChooseAddressDialog(Context context, String initProvinceCode, String initCityCode, String initDistrictCode) {
        this(context, initProvinceCode, initCityCode, initDistrictCode, false);
    }

    /**
     * 使用 code 或 name 初始化
     * note：省市区 code 或 name 需要一致
     *
     * @param context      上下文
     * @param initProvince 省 code 或 name
     * @param initCity     市 code 或 name
     * @param initDistrict 区 code 或 name
     * @param isName       是否是 name
     */
    public ChooseAddressDialog(Context context, String initProvince, String initCity, String initDistrict, boolean isName) {
        super(context, R.style.Theme_Light_NoTitle_Dialog);
        this.mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_choose_address, null);
        this.setContentView(view);
        setBottomLayout();
        if (!TextUtils.isEmpty(initProvince) && !TextUtils.isEmpty(initCity) && !TextUtils.isEmpty(initDistrict)) {
            if (isName) {
                this.mInitProvinceName = initProvince;
                this.mInitCityName = initCity;
                this.mInitDistrictName = initDistrict;
            } else {
                this.mInitProvinceCode = initProvince;
                this.mInitCityCode = initCity;
                this.mInitDistrictCode = initDistrict;
            }
            this.mInitIsByName = isName;
        }
        init();
    }

    /**
     * 设置 dialog 位于屏幕底部，并且设置出入动画
     */
    private void setBottomLayout() {
        Window win = getWindow();
        if (win != null) {
            win.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            win.setAttributes(lp);
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.Animation_Bottom);
        }
    }

    /**
     * 初始化控件  绑定事件
     */
    private void init() {
        mNpProvinces = (NumberPicker) findViewById(R.id.np_choose_address_province);
        mNpCities = (NumberPicker) findViewById(R.id.np_choose_address_city);
        mNpDistricts = (NumberPicker) findViewById(R.id.np_choose_address_districts);
        mBtnAffirm = (Button) findViewById(R.id.btn_choose_address_affirm);

        initLocate();
        //使NumberPicker不弹出输入框
        mNpProvinces.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mNpCities.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mNpDistricts.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mBtnAffirm.setOnClickListener(this);

        mNpProvinces.setOnValueChangedListener(this);
        mNpCities.setOnValueChangedListener(this);
        mNpDistricts.setOnValueChangedListener(this);


    }


    private List<MProvince> mListMProvinces = new ArrayList<>();

    private static class MProvince {
        public String id;
        public String name;
        List<MCity> cities;
    }

    private static class MCity {
        public String id;
        public String name;
        List<MDistrict> mMDistricts;
    }

    private static class MDistrict {
        public String id;
        public String name;
    }

    /**
     * 初始化省市区数据
     */
    private void initLocate() {
        try {
            InputStream inputStream = mContext.getAssets().open("city.json");

            JSONReader reader = new JSONReader(new InputStreamReader(inputStream));
            reader.startObject();
            while (reader.hasNext()) {

                if ("p".equals(reader.readString())) {
                    Object objP = reader.readObject();
                    mListMProvinces = JSON.parseArray(JSON.toJSONString(objP), MProvince.class);
                }
                if ("c".equals(reader.readString())) {
                    JSONObject objC = (JSONObject) reader.readObject();
                    for (int i = 0; i < mListMProvinces.size(); i++) {
                        String provinceId = mListMProvinces.get(i).id;
                        JSONArray objCc = null;
                        for (int j = 0; j < objC.size(); j++) {
                            objCc = (JSONArray) objC.get(provinceId);
                        }
                        mListMProvinces.get(i).cities = JSON.parseArray(JSON.toJSONString(objCc), MCity.class);
                    }
                }
                if ("a".equals(reader.readString())) {
                    JSONObject objD = (JSONObject) reader.readObject();
                    for (int i = 0; i < mListMProvinces.size(); i++) {
                        for (int k = 0; k < mListMProvinces.get(i).cities.size(); k++) {
                            String cityId = mListMProvinces.get(i).cities.get(k).id;
                            JSONArray objCc = null;
                            for (int j = 0; j < objD.size(); j++) {
                                objCc = (JSONArray) objD.get(cityId);
                            }
                            mListMProvinces.get(i).cities.get(k).mMDistricts = JSON.parseArray(JSON.toJSONString(objCc), MDistrict.class);
                        }
                    }
                }
            }
            reader.endObject();
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        //解析完成显示数据
        setView();
    }

    /**
     * 解析完成显示数据
     */
    private void setView() {

        if (!mListMProvinces.isEmpty()) {
            String[] displayProvinces1 = new String[mListMProvinces.size()];
            for (int i = 0; i < mListMProvinces.size(); i++) {
                displayProvinces1[i] = mListMProvinces.get(i).name;
            }
            mNpProvinces.setMaxValue(displayProvinces1.length - 1);
            mNpProvinces.setDisplayedValues(displayProvinces1);
            //设置分割线的高度
            setNumberPickerDividerHeight(mNpProvinces);
            setNumberPickerDividerHeight(mNpCities);
            setNumberPickerDividerHeight(mNpDistricts);

            //设置分割线的颜色
            setNumberPickerDividerColor(mNpProvinces);
            setNumberPickerDividerColor(mNpCities);
            setNumberPickerDividerColor(mNpDistricts);
            mCurrentMProvince = mListMProvinces.get(0);
            setDisplayCities(mCurrentMProvince);

            if (!mInitIsByName) {
                initPCD(mInitProvinceCode, mInitCityCode, mInitDistrictCode, false);
            } else { // name
                initPCD(mInitProvinceName, mInitCityName, mInitDistrictName, true);
            }
        }
    }

    /**
     * 回显省市区
     *
     * @param initProvince 省 code 或 name
     * @param initCity     市 code 或 name
     * @param initDistrict 区 code 或 name
     * @param isName       是否是 name
     */
    private void initPCD(String initProvince, String initCity, String initDistrict, boolean isName) {
        if (!TextUtils.isEmpty(initProvince) && !TextUtils.isEmpty(initCity) && !TextUtils.isEmpty(initDistrict)) {
            for (int i = 0; i < mListMProvinces.size(); i++) {
                if (isName ? initProvince.equals(mListMProvinces.get(i).name) : initProvince.equals(mListMProvinces.get(i).id)) {
                    mNpProvinces.setValue(i);
                    mCurrentMProvince = mListMProvinces.get(i);
                    setDisplayCities(mCurrentMProvince);
                    break;
                } else {
                    mNpProvinces.setValue(0);
                    mCurrentMProvince = mListMProvinces.get(0);
                }
            }

            if (mCurrentMProvince != null) {
                for (int i = 0; i < mCurrentMProvince.cities.size(); i++) {
                    if (isName ? initCity.equals(mCurrentMProvince.cities.get(i).name) : initCity.equals(mCurrentMProvince.cities.get(i).id)) {
                        mNpCities.setValue(i);
                        mCurrentMCity = mCurrentMProvince.cities.get(i);
                        setDisplayDistricts(mCurrentMCity);
                        break;
                    } else {
                        mNpCities.setValue(0);
                        mCurrentMCity = mCurrentMProvince.cities.get(0);
                    }
                }
            }
            if (mCurrentMCity != null) {
                for (int i = 0; i < mCurrentMCity.mMDistricts.size(); i++) {
                    if (isName ? initDistrict.equals(mCurrentMCity.mMDistricts.get(i).name) : initDistrict.equals(mCurrentMCity.mMDistricts.get(i).id)) {
                        mNpDistricts.setValue(i);
                        mCurrentMDistrict = mCurrentMCity.mMDistricts.get(i);
                        break;
                    } else {
                        mNpDistricts.setValue(0);
                        mCurrentMDistrict = mCurrentMCity.mMDistricts.get(0);
                    }
                }
            }
        }
    }

    private void setDisplayCities(MProvince MProvince) {

        String[] displayCities = new String[MProvince.cities.size()];
        for (int i = 0; i < MProvince.cities.size(); i++) {
            displayCities[i] = MProvince.cities.get(i).name;
        }
        mNpCities.setMaxValue(0);
        mNpCities.setDisplayedValues(displayCities);
        mNpCities.setMaxValue(displayCities.length - 1);

        mCurrentMCity = MProvince.cities.get(0);
        setDisplayDistricts(mCurrentMCity);
    }


    private void setDisplayDistricts(MCity MCity) {
        String[] displayDistricts = new String[MCity.mMDistricts.size()];
        for (int i = 0; i < MCity.mMDistricts.size(); i++) {
            displayDistricts[i] = MCity.mMDistricts.get(i).name;
        }
        mNpDistricts.setMaxValue(0);
        mNpDistricts.setDisplayedValues(displayDistricts);
        mNpDistricts.setMaxValue(displayDistricts.length - 1);

        mCurrentMDistrict = MCity.mMDistricts.get(0);
    }


    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        switch (picker.getId()) {
            case R.id.np_choose_address_province:
                mCurrentMProvince = mListMProvinces.get(newVal);
                setDisplayCities(mCurrentMProvince);
                break;
            case R.id.np_choose_address_city:
                mCurrentMCity = mCurrentMProvince.cities.get(newVal);
                setDisplayDistricts(mCurrentMCity);
                break;
            case R.id.np_choose_address_districts:
                mCurrentMDistrict = mCurrentMCity.mMDistricts.get(newVal);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_choose_address_affirm: // 确认
                Province province = new Province(mCurrentMProvince.id, mCurrentMProvince.name);
                City city = new City(mCurrentMCity.id, mCurrentMCity.name);
                District districtFor = new District(mCurrentMDistrict.id, mCurrentMDistrict.name);
                if (mOnChoiceCompleteListeners != null) {
                    mOnChoiceCompleteListeners.onChoiceComplete(province, city, districtFor);
                }
                cancel();
                break;
        }
    }

    /**
     * 用于设置回调数据
     * Province
     * City
     * District
     */
    public static class Province {
        public Province(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String id;
        public String name;
    }

    public static class City {
        public City(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String id;
        public String name;
    }

    public static class District {
        public District(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String id;
        public String name;
    }

    /**
     * 选择完成监听器
     */
    public interface OnChoiceCompleteListeners {
        void onChoiceComplete(Province province, City city, District districtFor);
    }

    private OnChoiceCompleteListeners mOnChoiceCompleteListeners;

    public void setChoiceCompleteListeners(OnChoiceCompleteListeners listeners) {
        this.mOnChoiceCompleteListeners = listeners;
    }

    //设置分割线的颜色
    private void setNumberPickerDividerColor(NumberPicker numberPicker) {
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    //设置分割线的颜色值this.getResources().getColor(R.color.green)
                    pf.set(numberPicker, new ColorDrawable(mContext.getResources().getColor(R.color.colorAccent)));
                } catch (Exception e) {
                    break;
                }
            }
        }
    }

    //设置分割线的高度
    private void setNumberPickerDividerHeight(NumberPicker numberPicker) {
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDividerHeight")) {
                pf.setAccessible(true);
                try {
                    pf.setInt(numberPicker, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

}
