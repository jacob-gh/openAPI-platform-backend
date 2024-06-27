package com.jacoe.openapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.jacoe.openapi.model.entity.UserInterfaceInfo;
import com.jacoe.openapi.service.UserInterfaceInfoService;
import com.jacoe.openapi.mapper.UserInterfaceInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author jacoe
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service实现
* @createDate 2024-06-27 17:26:53
*/
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService{

}




