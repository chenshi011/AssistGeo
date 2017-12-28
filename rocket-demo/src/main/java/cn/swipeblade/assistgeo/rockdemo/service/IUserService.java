package cn.swipeblade.assistgeo.rockdemo.service;

import cn.swipeblade.assistgeo.rockdemo.entity.User;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author blade
 * @since 2017-12-28
 */
public interface IUserService extends IService<User> {

    User findUserByCode(String code);
}
