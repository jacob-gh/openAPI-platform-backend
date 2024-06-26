package com.jacoe.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jacoe.project.model.entity.InterfaceInfo;
import com.jacoe.project.service.InterfaceInfoService;
import com.jacoe.project.mapper.InterfaceInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author jacoe
* @description 针对表【interface_info(接口信息)】的数据库操作Service实现
* @createDate 2024-06-21 15:26:08
*/
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
    implements InterfaceInfoService{

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean b) {

    }
}




