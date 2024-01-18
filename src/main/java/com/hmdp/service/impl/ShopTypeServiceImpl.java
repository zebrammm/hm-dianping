package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.hmdp.utils.RedisConstants.SHOP_TYPE_KEY;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Slf4j
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public Result queryList() {
        String key = SHOP_TYPE_KEY;
        String shopType = stringRedisTemplate.opsForValue().get(key);
        //已存在缓存
            if(StrUtil.isNotBlank(shopType)){
                List<ShopType> shopTypes = JSONUtil.toList(shopType,ShopType.class);
                return Result.ok(shopTypes);
            }
        //不存在，查询
        List<ShopType> shopTypes = query().orderByAsc("sort").list();
        //不存在
        if(shopTypes==null){
            return Result.fail("店铺类型不存在！");
        }
        //存在，存入redis
        stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(shopTypes));
        return Result.ok(shopTypes);

    }
}
