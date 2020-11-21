package com.molicloud.mqr.service.impl;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.molicloud.mqr.common.rest.ApiCode;
import com.molicloud.mqr.common.rest.ApiException;
import com.molicloud.mqr.entity.SysSetting;
import com.molicloud.mqr.enums.SettingEnums;
import com.molicloud.mqr.mapper.SysSettingMapper;
import com.molicloud.mqr.service.SysSettingService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 配置表 服务实现类
 * </p>
 *
 * @author feitao
 * @since 2020-11-07
 */
@Service
public class SysSettingServiceImpl extends ServiceImpl<SysSettingMapper, SysSetting> implements SysSettingService {

    @Override
    public <T> T getSysSettingByName(SettingEnums settingEnum, Class<T> clazz) {
        LambdaQueryWrapper<SysSetting> lambdaQueryWrapper = Wrappers.<SysSetting>lambdaQuery();
        lambdaQueryWrapper.eq(SysSetting::getName, settingEnum.getName());
        lambdaQueryWrapper.last(" limit 1");
        SysSetting sysSetting = baseMapper.selectOne(lambdaQueryWrapper);
        if (sysSetting == null) {
            return null;
        }
        JSONObject jsonObject = new JSONObject(sysSetting.getValue());
        return jsonObject.toBean(clazz);
    }

    @Override
    public <T> boolean saveSysSetting(SettingEnums settingEnum, Object dto, Class<T> clazz) {
        SysSetting sysSetting = baseMapper.selectOne(Wrappers.<SysSetting>lambdaQuery().eq(SysSetting::getName, settingEnum.getName()));
        try {
            T object = null;
            if (sysSetting == null) {
                object = clazz.newInstance();
                sysSetting = new SysSetting();
            } else {
                object = new JSONObject(sysSetting.getValue()).toBean(clazz);
            }
            // 复制dto数据到目标对象
            BeanUtils.copyProperties(dto, object);
            // 保存配置
            sysSetting.setName(settingEnum.getName());
            sysSetting.setValue(new JSONObject(object).toString());
            sysSetting.setRemark(settingEnum.getRemark());
            this.saveOrUpdate(sysSetting);
        } catch (Exception e) {
            throw new ApiException(ApiCode.SYSERR);
        }
        return true;
    }
}