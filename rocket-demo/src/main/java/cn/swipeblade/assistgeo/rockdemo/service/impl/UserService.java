package cn.swipeblade.assistgeo.rockdemo.service.impl;

import cn.swipeblade.assistgeo.rockdemo.entity.User;
import cn.swipeblade.assistgeo.rockdemo.mapper.UserMapper;
import cn.swipeblade.assistgeo.rockdemo.service.IUserService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author blade
 * @since 2017-12-28
 */
@Service
@CacheConfig(cacheNames = {"roc_user_cache"})
@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
public class UserService extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    @Cacheable(key = "#p0")
    public User findUserByCode(String code) {
        EntityWrapper<User> wrapper = new EntityWrapper<>();
        wrapper.eq("code", code);
        return selectOne(wrapper);
    }
}
