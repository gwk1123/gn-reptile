package com.gn.reptile.dome.manage.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author gwk
 * @since 2021-12-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("quarantine")
@ApiModel(value="Quarantine对象", description="")
public class Quarantine implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @ApiModelProperty(value = "外傭身份證明文件類型   ")
    private String idType;

    @ApiModelProperty(value = "外傭身份證明文件號碼")
    private String idNumber;

    @ApiModelProperty(value = "外傭姓名")
    private String name;

    @ApiModelProperty(value = "外傭飲食偏好")
    private String dietPreference;

    @ApiModelProperty(value = "外傭能以英語溝通")
    private String englishCommunication;

    @ApiModelProperty(value = "外傭能以廣東話溝通")
    private String cantoneseCommunication;

    @ApiModelProperty(value = "外傭能以普通話溝通")
    private String mandarinCommunicate;

    @ApiModelProperty(value = "聯絡人姓名")
    private String contactName;

    @ApiModelProperty(value = "聯絡人電郵地址")
    private String contactEmail;

    @ApiModelProperty(value = "強制檢疫開始日期")
    private String startDate;

    @ApiModelProperty(value = "聯絡人手提電話號碼")
    private String contactPhone;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    private Integer flay;

    private Integer lockFlag;
}
