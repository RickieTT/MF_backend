package com.rickie_job.mf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rickie_job.mf.model.domain.Tag;
import com.rickie_job.mf.service.TagService;
import com.rickie_job.mf.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
* @author rickie
* @description 针对表【tag(标签)】的数据库操作Service实现
* @createDate 2022-12-09 14:26:11
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




