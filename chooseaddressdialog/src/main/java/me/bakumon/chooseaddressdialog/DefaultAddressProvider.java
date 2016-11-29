package me.bakumon.chooseaddressdialog;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mafei on 2016/11/29 12:18.
 *
 * @author bakumon
 * @version 1.0.0
 * @class DefaultAddressProvider
 * @describe 省市区数据提供者默认实现类 city.json
 */
public class DefaultAddressProvider implements IAddressProvider {

    private List<ChooseAddressDialog.Province> mListProvinces = new ArrayList<>();


    @Override
    public List<ChooseAddressDialog.Province> getAddressData(Context context) {
        try {
            InputStream inputStream = context.getAssets().open("city.json");

            JSONReader reader = new JSONReader(new InputStreamReader(inputStream));
            reader.startObject();
            while (reader.hasNext()) {
                if ("p".equals(reader.readString())) {
                    Object objP = reader.readObject();
                    mListProvinces = JSON.parseArray(JSON.toJSONString(objP), ChooseAddressDialog.Province.class);
                }
                if ("c".equals(reader.readString())) {
                    JSONObject objC = (JSONObject) reader.readObject();
                    for (int i = 0; i < mListProvinces.size(); i++) {
                        String provinceId = mListProvinces.get(i).id;
                        JSONArray objCc = null;
                        for (int j = 0; j < objC.size(); j++) {
                            objCc = (JSONArray) objC.get(provinceId);
                        }
                        mListProvinces.get(i).cities = JSON.parseArray(JSON.toJSONString(objCc), ChooseAddressDialog.City.class);
                    }
                }
                if ("a".equals(reader.readString())) {
                    JSONObject objD = (JSONObject) reader.readObject();
                    for (int i = 0; i < mListProvinces.size(); i++) {
                        for (int k = 0; k < mListProvinces.get(i).cities.size(); k++) {
                            String cityId = mListProvinces.get(i).cities.get(k).id;
                            JSONArray objCc = null;
                            for (int j = 0; j < objD.size(); j++) {
                                objCc = (JSONArray) objD.get(cityId);
                            }
                            mListProvinces.get(i).cities.get(k).mAreas = JSON.parseArray(JSON.toJSONString(objCc), ChooseAddressDialog.Area.class);
                        }
                    }
                }
            }
            reader.endObject();
            reader.close();
            return mListProvinces;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
