package top.liujingyanghui.assignmentupload.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.liujingyanghui.assignmentupload.entity.City;
import top.liujingyanghui.assignmentupload.entity.School;
import top.liujingyanghui.assignmentupload.service.CityService;
import top.liujingyanghui.assignmentupload.service.SchoolService;
import top.liujingyanghui.assignmentupload.vo.Result;
import top.liujingyanghui.assignmentupload.vo.ResultEnum;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 学校控制器
 */
@RestController
@RequestMapping("api/school")
public class SchoolController {

    @Autowired
    private CityService cityService;

    @Autowired
    private SchoolService schoolService;

    /**
     * 分页获取学校信息
     *
     * @param provinceId
     * @param cityId
     * @param current
     * @param size
     * @return
     */
    @GetMapping("page")
    public Result page(@RequestParam int provinceId, @RequestParam int cityId, @RequestParam(defaultValue = "1") int current, @RequestParam(defaultValue = "10") int size,
                       @RequestParam String searchKey) {
        Page<School> schoolPage = new Page<>(current, size);
        IPage<School> page = null;
        if (-1 == provinceId && -1 == cityId) {
            // 省份全选
            page = schoolService.page(schoolPage, Wrappers.<School>lambdaQuery().eq(School::getDelFlag, 0).eq(School::getVerifyFlag, 1).like(School::getName, searchKey));
        } else if (-1 == cityId) {
            List<City> cityList = cityService.list(Wrappers.<City>lambdaQuery().eq(City::getProvinceId, provinceId));
            if (cityList.size() == 0) {
//                return Result.error("没有发现学校");
                return Result.success(ResultEnum.SUCCESS);
            }
            Set<Integer> ids = cityList.stream().map(item -> item.getId()).collect(Collectors.toSet());
            page = schoolService.page(schoolPage, Wrappers.<School>lambdaQuery().in(School::getCityId, ids).eq(School::getDelFlag, 0).eq(School::getVerifyFlag, 1)
                    .like(School::getName, searchKey));
        } else {
            page = schoolService.page(schoolPage, Wrappers.<School>lambdaQuery().eq(School::getCityId, cityId).eq(School::getDelFlag, 0).eq(School::getVerifyFlag, 1)
                    .like(School::getName, searchKey));
        }
        return Result.success(page);
    }

    /**
     * 修改学校名
     *
     * @param school
     * @return
     */
    @PutMapping("update")
    public Result update(@RequestBody School school) {
        School one = new School();
        one.setId(school.getId());
        one.setName(school.getName());
        return schoolService.updateById(one) ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("delete")
    public Result delete(@RequestParam int id) {

        // 待加入判读是否删除


        School school = new School();
        school.setId(id);
        school.setDelFlag(1);
        return schoolService.updateById(school) ? Result.success() : Result.error("删除失败");
    }

    /**
     * 多选删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping("deletes")
    public Result deletes(@RequestParam String[] ids) {
        boolean update = schoolService.update(Wrappers.<School>lambdaUpdate().in(School::getId, ids).set(School::getDelFlag, 1));
        return update ? Result.success() : Result.error();
    }
}
