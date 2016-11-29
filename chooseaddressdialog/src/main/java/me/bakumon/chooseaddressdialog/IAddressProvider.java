package me.bakumon.chooseaddressdialog;

import android.content.Context;

import java.util.List;

/**
 * Created by bakumon on 2016/11/29 12:15.
 *
 * @author mafei
 * @version 1.0.0
 * @class IAddressProvider
 * @describe 省市区数据提供者
 */
public interface IAddressProvider {
    List<ChooseAddressDialog.Province> getAddressData(Context context);
}
