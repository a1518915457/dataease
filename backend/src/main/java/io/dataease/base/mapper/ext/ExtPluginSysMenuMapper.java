package io.dataease.base.mapper.ext;

import io.dataease.base.mapper.ext.query.GridExample;
import io.dataease.controller.sys.request.SimpleTreeNode;
import io.dataease.plugins.common.dto.PluginSysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtPluginSysMenuMapper {

    void savePluginMenu(@Param("menuList") List<PluginSysMenu> menuList);

    void deletePluginMenu();
}