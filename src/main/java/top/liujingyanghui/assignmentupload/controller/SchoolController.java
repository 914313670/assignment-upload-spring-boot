package top.liujingyanghui.assignmentupload.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.liujingyanghui.assignmentupload.entity.City;
import top.liujingyanghui.assignmentupload.entity.School;
import top.liujingyanghui.assignmentupload.service.CityService;
import top.liujingyanghui.assignmentupload.service.SchoolService;
import top.liujingyanghui.assignmentupload.vo.Result;

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
     * @param provinceId
     * @param cityId
     * @param current
     * @param size
     * @return
     */
    @GetMapping("page")
    public Result page(@RequestParam int provinceId, @RequestParam int cityId, @RequestParam(defaultValue = "1") int current, @RequestParam(defaultValue = "10") int size) {
        Page<School> schoolPage = new Page<>(current, size);
        IPage<School> page = null;
        if (-1 == provinceId && -1 == cityId) {
            // 省份全选
            page = schoolService.page(schoolPage,Wrappers.<School>lambdaQuery().eq(School::getDelFlag,0).eq(School::getVerifyFlag,1));
        } else if (-1 == cityId) {
            List<City> cityList = cityService.list(Wrappers.<City>lambdaQuery().eq(City::getProvinceId, provinceId));
            Set<Integer> ids = cityList.stream().map(item -> item.getId()).collect(Collectors.toSet());
            page = schoolService.page(schoolPage, Wrappers.<School>lambdaQuery().in(School::getCityId, ids).eq(School::getDelFlag,0).eq(School::getVerifyFlag,1));
        } else {
            page = schoolService.page(schoolPage, Wrappers.<School>lambdaQuery().eq(School::getCityId, cityId).eq(School::getDelFlag,0).eq(School::getVerifyFlag,1));
        }
        return Result.success(page);
    }
}
