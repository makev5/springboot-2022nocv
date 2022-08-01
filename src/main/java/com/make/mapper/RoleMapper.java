package com.make.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.make.entity.Role;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    @Select("select mid from role_menu where rid = #{roleId}")
    List<Integer> queryAllPermissionByRid(Integer roleId);

    @Delete("delete from role_menu where rid = #{rid}")
    void deleteRoleByRid(Integer rid);

    @Insert("insert into role_menu (rid, mid) values (#{rid}, #{mid})")
    void saveRoleMenu(@Param("rid") Integer rid, @Param("mid") Integer mid);

    @Select("select rid from user_role where uid = #{id}")
    List<Integer> queryUserRoleById(Integer id);

    @Insert("insert into user_role (uid, rid) values (#{uid}, #{rid})")
    void saveUserRole(@Param("uid")Integer uid,@Param("rid") Integer rid);

    @Delete("delete from user_role where uid = #{uid}")
    void deleteRoleUserByUid(Integer uid);
}
