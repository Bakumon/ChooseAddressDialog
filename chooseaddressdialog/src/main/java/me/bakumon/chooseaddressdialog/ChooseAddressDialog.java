package me.bakumon.chooseaddressdialog;

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
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


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

    private Province mCurrentProvince;
    private City mCurrentCity;
    private Area mCurrentArea;

    private boolean mInitIsByName; // 是否是用 name 回显 true：name false：id
    private Context mContext;


    private List<Province> mListProvinces = new ArrayList<>();

    public ChooseAddressDialog(Context context) {
        super(context, R.style.Theme_Light_NoTitle_Dialog);
        this.mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_choose_address, null);
        this.setContentView(view);

        initView();
    }

    /**
     * 设置省市区数据
     *
     * @param listProvinces List<Province>
     * @return this
     */
    public ChooseAddressDialog setAddressData(List<Province> listProvinces) {
        mListProvinces = listProvinces;
        return this;
    }

    /**
     * 通过 code 回显省市区
     *
     * @param initProvinceCode 省 code
     * @param initCityCode     市 code
     * @param initAreaCode     区 code
     * @return this
     */
    public ChooseAddressDialog setInitPCDCode(String initProvinceCode, String initCityCode, String initAreaCode) {

        if (TextUtils.isEmpty(initProvinceCode) && TextUtils.isEmpty(initCityCode) && TextUtils.isEmpty(initAreaCode)) {
            return this;
        }

        mCurrentProvince = new Province();
        mCurrentProvince.id = initProvinceCode;

        mCurrentCity = new City();
        mCurrentCity.id = initCityCode;

        mCurrentArea = new Area();
        mCurrentArea.id = initAreaCode;

        mInitIsByName = false;
        return this;
    }

    /**
     * 通过 name 回显省市区
     *
     * @param initProvinceName 省 name
     * @param initCityName     市 name
     * @param initDistrictName 区 name
     * @return this
     */
    public ChooseAddressDialog setInitPCDName(String initProvinceName, String initCityName, String initDistrictName) {

        if (TextUtils.isEmpty(initProvinceName) && TextUtils.isEmpty(initCityName) && TextUtils.isEmpty(initDistrictName)) {
            return this;
        }

        mCurrentProvince = new Province();
        mCurrentProvince.name = initProvinceName;

        mCurrentCity = new City();
        mCurrentCity.name = initCityName;

        mCurrentArea = new Area();
        mCurrentArea.name = initDistrictName;

        mInitIsByName = true;
        return this;
    }

    @Override
    public void show() {
        if (mListProvinces.isEmpty()) {
            Toast.makeText(mContext, "需要设置省市区数据", Toast.LENGTH_SHORT).show();
            return;
        }
        setView();
        if (!isShowing()) {
            super.show();
        }
    }


    /**
     * 显示数据
     */
    private void setView() {

        if (!mListProvinces.isEmpty()) {
            String[] displayProvinces1 = new String[mListProvinces.size()];
            for (int i = 0; i < mListProvinces.size(); i++) {
                displayProvinces1[i] = mListProvinces.get(i).name;
            }
            mNpProvinces.setMaxValue(displayProvinces1.length - 1);
            mNpProvinces.setDisplayedValues(displayProvinces1);

            if (mCurrentProvince != null) {
                if (mInitIsByName) {
                    initPCD(mCurrentProvince.name, mCurrentCity.name, mCurrentArea.name, true);
                } else { // name
                    initPCD(mCurrentProvince.id, mCurrentCity.id, mCurrentArea.id, false);
                }
            } else {
                mCurrentProvince = mListProvinces.get(0);
                setDisplayCities(mCurrentProvince);
            }
        }
    }

    /**
     * 回显省市区
     *
     * @param initProvince 省 id 或 name
     * @param initCity     市 id 或 name
     * @param initDistrict 区 id 或 name
     * @param isName       是否是 name
     */
    private void initPCD(String initProvince, String initCity, String initDistrict, boolean isName) {
        if (!TextUtils.isEmpty(initProvince) && !TextUtils.isEmpty(initCity) && !TextUtils.isEmpty(initDistrict)) {
            for (int i = 0; i < mListProvinces.size(); i++) {
                if (isName ? initProvince.equals(mListProvinces.get(i).name) : initProvince.equals(mListProvinces.get(i).id)) {
                    mNpProvinces.setValue(i);
                    mCurrentProvince = mListProvinces.get(i);
                    setDisplayCities(mCurrentProvince);
                    break;
                } else {
                    mNpProvinces.setValue(0);
                    mCurrentProvince = mListProvinces.get(0);
                }
            }

            if (mCurrentProvince != null) {
                for (int i = 0; i < mCurrentProvince.cities.size(); i++) {
                    if (isName ? initCity.equals(mCurrentProvince.cities.get(i).name) : initCity.equals(mCurrentProvince.cities.get(i).id)) {
                        mNpCities.setValue(i);
                        mCurrentCity = mCurrentProvince.cities.get(i);
                        setDisplayDistricts(mCurrentCity);
                        break;
                    } else {
                        mNpCities.setValue(0);
                        mCurrentCity = mCurrentProvince.cities.get(0);
                    }
                }
            }
            if (mCurrentCity != null) {
                for (int i = 0; i < mCurrentCity.mAreas.size(); i++) {
                    if (isName ? initDistrict.equals(mCurrentCity.mAreas.get(i).name) : initDistrict.equals(mCurrentCity.mAreas.get(i).id)) {
                        mNpDistricts.setValue(i);
                        mCurrentArea = mCurrentCity.mAreas.get(i);
                        break;
                    } else {
                        mNpDistricts.setValue(0);
                        mCurrentArea = mCurrentCity.mAreas.get(0);
                    }
                }
            }
        }
    }

    /**
     * 设置省对应的所有市
     */
    private void setDisplayCities(Province Province) {

        String[] displayCities = new String[Province.cities.size()];
        for (int i = 0; i < Province.cities.size(); i++) {
            displayCities[i] = Province.cities.get(i).name;
        }
        mNpCities.setMaxValue(0);
        mNpCities.setDisplayedValues(displayCities);
        mNpCities.setMaxValue(displayCities.length - 1);

        mCurrentCity = Province.cities.get(0);
        setDisplayDistricts(mCurrentCity);
    }

    /**
     * 设置市对应的所有区
     */
    private void setDisplayDistricts(City City) {
        String[] displayDistricts = new String[City.mAreas.size()];
        for (int i = 0; i < City.mAreas.size(); i++) {
            displayDistricts[i] = City.mAreas.get(i).name;
        }
        mNpDistricts.setMaxValue(0);
        mNpDistricts.setDisplayedValues(displayDistricts);
        mNpDistricts.setMaxValue(displayDistricts.length - 1);

        mCurrentArea = City.mAreas.get(0);
    }


    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        if (picker.getId() == R.id.np_choose_address_province) { // 省 NumberPicker
            mCurrentProvince = mListProvinces.get(newVal);
            setDisplayCities(mCurrentProvince);
        } else if (picker.getId() == R.id.np_choose_address_city) { // 市 NumberPicker
            mCurrentCity = mCurrentProvince.cities.get(newVal);
            setDisplayDistricts(mCurrentCity);
        } else { // 区 NumberPicker
            mCurrentArea = mCurrentCity.mAreas.get(newVal);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_choose_address_affirm) {
            if (mOnChoiceCompleteListeners != null) {
                mOnChoiceCompleteListeners.onChoiceComplete(mCurrentProvince, mCurrentCity, mCurrentArea);
            }
            cancel();
        }
    }

    /**
     * 初始化控件  绑定事件
     */
    private void initView() {
        mNpProvinces = (NumberPicker) findViewById(R.id.np_choose_address_province);
        mNpCities = (NumberPicker) findViewById(R.id.np_choose_address_city);
        mNpDistricts = (NumberPicker) findViewById(R.id.np_choose_address_districts);
        mBtnAffirm = (Button) findViewById(R.id.btn_choose_address_affirm);

        //设置分割线的高度
        setNumberPickerDividerHeight(mNpProvinces);
        setNumberPickerDividerHeight(mNpCities);
        setNumberPickerDividerHeight(mNpDistricts);
        //设置分割线的颜色
        setNumberPickerDividerColor(mNpProvinces);
        setNumberPickerDividerColor(mNpCities);
        setNumberPickerDividerColor(mNpDistricts);
        //使NumberPicker不弹出输入框
        mNpProvinces.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mNpCities.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mNpDistricts.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        mBtnAffirm.setOnClickListener(this);
        mNpProvinces.setOnValueChangedListener(this);
        mNpCities.setOnValueChangedListener(this);
        mNpDistricts.setOnValueChangedListener(this);

        setBottomLayout();
    }

    /**
     * 选择完成监听器
     */
    public interface OnChoiceCompleteListeners {
        void onChoiceComplete(Province province, City city, Area areaFor);
    }

    private OnChoiceCompleteListeners mOnChoiceCompleteListeners;

    public ChooseAddressDialog setChoiceCompleteListeners(OnChoiceCompleteListeners listeners) {
        this.mOnChoiceCompleteListeners = listeners;
        return this;
    }

    /**
     * 设置 dialog 位于屏幕底部，并且设置出入场动画
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
     * 设置分割线的颜色
     */
    private void setNumberPickerDividerColor(NumberPicker numberPicker) {
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    //设置分割线的颜色
                    pf.set(numberPicker, new ColorDrawable(mContext.getResources().getColor(R.color.colorHalvingLine)));
                } catch (Exception e) {
                    break;
                }
            }
        }
    }

    /**
     * 设置分割线的高度
     */
    private void setNumberPickerDividerHeight(NumberPicker numberPicker) {
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDividerHeight")) {
                pf.setAccessible(true);
                try {
                    // 设置分割线的高度 1 px
                    pf.setInt(numberPicker, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public static class Province {
        public String id;
        public String name;
        public List<City> cities;
    }

    public static class City {
        public String id;
        public String name;
        public List<Area> mAreas;
    }

    public static class Area {
        public String id;
        public String name;
    }
}
