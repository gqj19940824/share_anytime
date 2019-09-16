package com.unity.resource.controller;

import com.alibaba.fastjson.JSONObject;
import com.unity.common.base.controller.BaseWebController;
import com.unity.common.exception.UnityRuntimeException;
import com.unity.common.pojos.SystemConfiguration;
import com.unity.resource.entity.FastdfsFileDTO;
import com.unity.resource.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/editor")
public class UeditorController extends BaseWebController {

    private final FileService fileService;

    public UeditorController( FileService fileService) {
        this.fileService = fileService;
    }





    @RequestMapping("vueConfig")
    public void ueditorConfig(HttpServletRequest request, HttpServletResponse response, MultipartFile file) {
        try {
            String exec = "";
            request.setCharacterEncoding("utf-8");
            response.setContentType("text/plain");
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            String actionType = request.getParameter("action");
            String callback = request.getParameter("callback");
            System.out.println(callback);
            // 获取配置
            if ("config".equals(actionType)) {
                exec = StringUtils.isNotEmpty(callback) ? callback + "(" + getConfig() + ")" : getConfig();
            } else {
                if (file == null || file.isEmpty()) {
                    exec =  "error";
                }else {
                    FastdfsFileDTO fileDTO = fileService.fileUpload(file, false);
                    exec = resultMap(fileDTO);
                }
            }
            PrintWriter writer = response.getWriter();
            writer.write(exec);
            writer.flush();
            writer.close();

        } catch (Exception e) {
            logger.info("上传错误");
        }
    }


    private String resultMap(FastdfsFileDTO fileDTO){
        Map<String ,Object> result = new HashMap<>();
        result.put("state","SUCCESS");
        result.put("original",fileDTO.getFileName());
        result.put("title",fileDTO.getFileName());
        result.put("url", fileDTO.getFileUrl());
        JSONObject jsonResult = new JSONObject(result);
        return jsonResult.toString();
    }

    private String getConfig() {

        String config = "{\"imageActionName\": \"uploadimage\", \t\"imageFieldName\": \"file\", \t\"imageMaxSize\": 2048000, \t\"imageAllowFiles\": [\".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\"], \t\"imageCompressEnable\": true, \t\"imageCompressBorder\": 1600, \t\"imageInsertAlign\": \"none\", \t\"imageUrlPrefix\": \"\", \t\"imagePathFormat\": \"\\/storage\\/image\\/{yyyy}{mm}{dd}\\/{time}{rand:6}\", \t\"scrawlActionName\": \"uploadscrawl\", \t\"scrawlFieldName\": \"file\", \t\"scrawlPathFormat\": \"\\/storage\\/image\\/{yyyy}{mm}{dd}\\/{time}{rand:6}\", \t\"scrawlMaxSize\": 2048000, \t\"scrawlUrlPrefix\": \"\", \t\"scrawlInsertAlign\": \"none\", \t\"snapscreenActionName\": \"uploadimage\", \t\"snapscreenPathFormat\": \"\\/storage\\/image\\/{yyyy}{mm}{dd}\\/{time}{rand:6}\", \t\"snapscreenUrlPrefix\": \"\", \t\"snapscreenInsertAlign\": \"none\", \t\"catcherLocalDomain\": [\"127.0.0.1\", \"localhost\", \"img.baidu.com\"], \t\"catcherActionName\": \"catchimage\", \t\"catcherFieldName\": \"source\", \t\"catcherPathFormat\": \"\\/storage\\/image\\/{yyyy}{mm}{dd}\\/{time}{rand:6}\", \t\"catcherUrlPrefix\": \"\", \t\"catcherMaxSize\": 2048000, \t\"catcherAllowFiles\": [\".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\"], \t\"videoActionName\": \"uploadvideo\", \t\"videoFieldName\": \"file\", \t\"videoPathFormat\": \"\\/storage\\/video\\/{yyyy}{mm}{dd}\\/{time}{rand:6}\", \t\"videoUrlPrefix\": \"\", \t\"videoMaxSize\": 102400000, \t\"videoAllowFiles\": [\".flv\", \".swf\", \".mkv\", \".avi\", \".rm\", \".rmvb\", \".mpeg\", \".mpg\", \".ogg\", \".ogv\", \".mov\", \".wmv\", \".mp4\", \".webm\", \".mp3\", \".wav\", \".mid\"], \t\"fileActionName\": \"uploadfile\", \t\"fileFieldName\": \"file\", \t\"filePathFormat\": \"\\/storage\\/file\\/{yyyy}{mm}{dd}\\/{time}{rand:6}\", \t\"fileUrlPrefix\": \" \", \t\"fileMaxSize\": 51200000, \t\"fileAllowFiles\": [\".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\", \".flv\", \".swf\", \".mkv\", \".avi\", \".rm\", \".rmvb\", \".mpeg\", \".mpg\", \".ogg\", \".ogv\", \".mov\", \".wmv\", \".mp4\", \".webm\", \".mp3\", \".wav\", \".mid\", \".rar\", \".zip\", \".tar\", \".gz\", \".7z\", \".bz2\", \".cab\", \".iso\", \".doc\", \".docx\", \".xls\", \".xlsx\", \".ppt\", \".pptx\", \".pdf\", \".txt\", \".md\", \".xml\"], \t\"imageManagerActionName\": \"listimage\", \t\"imageManagerListPath\": \"\\/storage\\/image\\/\", \t\"imageManagerListSize\": 20, \t\"imageManagerUrlPrefix\": \" \", \t\"imageManagerInsertAlign\": \"none\", \t\"imageManagerAllowFiles\": [\".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\"], \t\"fileManagerActionName\": \"listfile\", \t\"fileManagerListPath\": \"\\/storage\\/file\\/\", \t\"fileManagerUrlPrefix\": \"\", \t\"fileManagerListSize\": 20, \t\"fileManagerAllowFiles\": [\".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\", \".flv\", \".swf\", \".mkv\", \".avi\", \".rm\", \".rmvb\", \".mpeg\", \".mpg\", \".ogg\", \".ogv\", \".mov\", \".wmv\", \".mp4\", \".webm\", \".mp3\", \".wav\", \".mid\", \".rar\", \".zip\", \".tar\", \".gz\", \".7z\", \".bz2\", \".cab\", \".iso\", \".doc\", \".docx\", \".xls\", \".xlsx\", \".ppt\", \".pptx\", \".pdf\", \".txt\", \".md\", \".xml\"] }";
        return config;

    }




    /**
     * 上传文件 到fastdfs文件服务器
     *
     * @param file 需要上传的文件
     * @return 保存后的文件地址，文件组名
     * @author Jung
     * @since 2018年06月16日12:17:42
     */
    @PostMapping("upload")
    public String  upload(@RequestParam(required = false) MultipartFile file) {
        //fastdfs 文件服务器地址
        if (file == null || file.isEmpty()) {
            return "error";
        }
        try {
            FastdfsFileDTO fileDTO = fileService.fileUpload(file, false);
            String config = "{\"state\": \"SUCCESS\"," +
                    "\"url\": \"" + fileDTO.getFileUrl() + "\"," +
                    "\"title\": \"" + fileDTO.getFileName() + "\"," +
                    "\"original\": \"" + fileDTO.getFileName() + "\"}";
            return config;
        } catch (UnityRuntimeException e) {
            return "error";
        }
    }


    @RequestMapping(value = "/config")
    @ResponseBody
    public String config(HttpServletRequest request, HttpServletResponse response) {

        String config = "{\n" +
                "    /* 上传图片配置项 */\n" +
                "    \"imageActionName\": \"uploadimage\", /* 执行上传图片的action名称 */\n" +
                "    \"imageFieldName\": \"file\", /* 提交的图片表单名称 */\n" +
                "    \"imageMaxSize\": 2048000, /* 上传大小限制，单位B */\n" +
                "    \"imageAllowFiles\": [\".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\"], /* 上传图片格式显示 */\n" +
                "    \"imageCompressEnable\": true, /* 是否压缩图片,默认是true */\n" +
                "    \"imageCompressBorder\": 1600, /* 图片压缩最长边限制 */\n" +
                "    \"imageInsertAlign\": \"none\", /* 插入的图片浮动方式 */\n" +
                "    \"imageUrlPrefix\": \"\", /* 图片访问路径前缀 */\n" +
                "    \"imagePathFormat\": \"/ueditor/jsp/upload/image/{yyyy}{mm}{dd}/{time}{rand:6}\", /* 上传保存路径,可以自定义保存路径和文件名格式 */\n" +
                "                                /* {filename} 会替换成原文件名,配置这项需要注意中文乱码问题 */\n" +
                "                                /* {rand:6} 会替换成随机数,后面的数字是随机数的位数 */\n" +
                "                                /* {time} 会替换成时间戳 */\n" +
                "                                /* {yyyy} 会替换成四位年份 */\n" +
                "                                /* {yy} 会替换成两位年份 */\n" +
                "                                /* {mm} 会替换成两位月份 */\n" +
                "                                /* {dd} 会替换成两位日期 */\n" +
                "                                /* {hh} 会替换成两位小时 */\n" +
                "                                /* {ii} 会替换成两位分钟 */\n" +
                "                                /* {ss} 会替换成两位秒 */\n" +
                "                                /* 非法字符 \\ : * ? \" < > | */\n" +
                "                                /* 具请体看线上文档: fex.baidu.com/ueditor/#use-format_upload_filename */\n" +
                "\n" +
                "    /* 涂鸦图片上传配置项 */\n" +
                "    \"scrawlActionName\": \"uploadscrawl\", /* 执行上传涂鸦的action名称 */\n" +
                "    \"scrawlFieldName\": \"file\", /* 提交的图片表单名称 */\n" +
                "    \"scrawlPathFormat\": \"/ueditor/jsp/upload/image/{yyyy}{mm}{dd}/{time}{rand:6}\", /* 上传保存路径,可以自定义保存路径和文件名格式 */\n" +
                "    \"scrawlMaxSize\": 2048000, /* 上传大小限制，单位B */\n" +
                "    \"scrawlUrlPrefix\": \"\", /* 图片访问路径前缀 */\n" +
                "    \"scrawlInsertAlign\": \"none\",\n" +
                "\n" +
                "    /* 截图工具上传 */\n" +
                "    \"snapscreenActionName\": \"uploadimage\", /* 执行上传截图的action名称 */\n" +
                "    \"snapscreenPathFormat\": \"/ueditor/jsp/upload/image/{yyyy}{mm}{dd}/{time}{rand:6}\", /* 上传保存路径,可以自定义保存路径和文件名格式 */\n" +
                "    \"snapscreenUrlPrefix\": \"\", /* 图片访问路径前缀 */\n" +
                "    \"snapscreenInsertAlign\": \"none\", /* 插入的图片浮动方式 */\n" +
                "\n" +
                "    /* 抓取远程图片配置 */\n" +
                "    \"catcherLocalDomain\": [\"127.0.0.1\", \"localhost\", \"img.baidu.com\"],\n" +
                "    \"catcherActionName\": \"catchimage\", /* 执行抓取远程图片的action名称 */\n" +
                "    \"catcherFieldName\": \"source\", /* 提交的图片列表表单名称 */\n" +
                "    \"catcherPathFormat\": \"/ueditor/jsp/upload/image/{yyyy}{mm}{dd}/{time}{rand:6}\", /* 上传保存路径,可以自定义保存路径和文件名格式 */\n" +
                "    \"catcherUrlPrefix\": \"\", /* 图片访问路径前缀 */\n" +
                "    \"catcherMaxSize\": 2048000, /* 上传大小限制，单位B */\n" +
                "    \"catcherAllowFiles\": [\".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\"], /* 抓取图片格式显示 */\n" +
                "\n" +
                "    /* 上传视频配置 */\n" +
                "    \"videoActionName\": \"uploadvideo\", /* 执行上传视频的action名称 */\n" +
                "    \"videoFieldName\": \"file\", /* 提交的视频表单名称 */\n" +
                "    \"videoPathFormat\": \"/ueditor/jsp/upload/video/{yyyy}{mm}{dd}/{time}{rand:6}\", /* 上传保存路径,可以自定义保存路径和文件名格式 */\n" +
                "    \"videoUrlPrefix\": \"\", /* 视频访问路径前缀 */\n" +
                "    \"videoMaxSize\": 102400000, /* 上传大小限制，单位B，默认100MB */\n" +
                "    \"videoAllowFiles\": [\n" +
                "        \".flv\", \".swf\", \".mkv\", \".avi\", \".rm\", \".rmvb\", \".mpeg\", \".mpg\",\n" +
                "        \".ogg\", \".ogv\", \".mov\", \".wmv\", \".mp4\", \".webm\", \".mp3\", \".wav\", \".mid\"], /* 上传视频格式显示 */\n" +
                "\n" +
                "    /* 上传文件配置 */\n" +
                "    \"fileActionName\": \"uploadfile\", /* controller里,执行上传视频的action名称 */\n" +
                "    \"fileFieldName\": \"file\", /* 提交的文件表单名称 */\n" +
                "    \"filePathFormat\": \"/ueditor/jsp/upload/file/{yyyy}{mm}{dd}/{time}{rand:6}\", /* 上传保存路径,可以自定义保存路径和文件名格式 */\n" +
                "    \"fileUrlPrefix\": \"\", /* 文件访问路径前缀 */\n" +
                "    \"fileMaxSize\": 51200000, /* 上传大小限制，单位B，默认50MB */\n" +
                "    \"fileAllowFiles\": [\n" +
                "        \".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\",\n" +
                "        \".flv\", \".swf\", \".mkv\", \".avi\", \".rm\", \".rmvb\", \".mpeg\", \".mpg\",\n" +
                "        \".ogg\", \".ogv\", \".mov\", \".wmv\", \".mp4\", \".webm\", \".mp3\", \".wav\", \".mid\",\n" +
                "        \".rar\", \".zip\", \".tar\", \".gz\", \".7z\", \".bz2\", \".cab\", \".iso\",\n" +
                "        \".doc\", \".docx\", \".xls\", \".xlsx\", \".ppt\", \".pptx\", \".pdf\", \".txt\", \".md\", \".xml\"\n" +
                "    ], /* 上传文件格式显示 */\n" +
                "\n" +
                "    /* 列出指定目录下的图片 */\n" +
                "    \"imageManagerActionName\": \"listimage\", /* 执行图片管理的action名称 */\n" +
                "    \"imageManagerListPath\": \"/ueditor/jsp/upload/image/\", /* 指定要列出图片的目录 */\n" +
                "    \"imageManagerListSize\": 20, /* 每次列出文件数量 */\n" +
                "    \"imageManagerUrlPrefix\": \"\", /* 图片访问路径前缀 */\n" +
                "    \"imageManagerInsertAlign\": \"none\", /* 插入的图片浮动方式 */\n" +
                "    \"imageManagerAllowFiles\": [\".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\"], /* 列出的文件类型 */\n" +
                "\n" +
                "    /* 列出指定目录下的文件 */\n" +
                "    \"fileManagerActionName\": \"listfile\", /* 执行文件管理的action名称 */\n" +
                "    \"fileManagerListPath\": \"/ueditor/jsp/upload/file/\", /* 指定要列出文件的目录 */\n" +
                "    \"fileManagerUrlPrefix\": \"\", /* 文件访问路径前缀 */\n" +
                "    \"fileManagerListSize\": 20, /* 每次列出文件数量 */\n" +
                "    \"fileManagerAllowFiles\": [\n" +
                "        \".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\",\n" +
                "        \".flv\", \".swf\", \".mkv\", \".avi\", \".rm\", \".rmvb\", \".mpeg\", \".mpg\",\n" +
                "        \".ogg\", \".ogv\", \".mov\", \".wmv\", \".mp4\", \".webm\", \".mp3\", \".wav\", \".mid\",\n" +
                "        \".rar\", \".zip\", \".tar\", \".gz\", \".7z\", \".bz2\", \".cab\", \".iso\",\n" +
                "        \".doc\", \".docx\", \".xls\", \".xlsx\", \".ppt\", \".pptx\", \".pdf\", \".txt\", \".md\", \".xml\"\n" +
                "    ] /* 列出的文件类型 */\n" +
                "\n" +
                "}";
        return config;

    }



}
