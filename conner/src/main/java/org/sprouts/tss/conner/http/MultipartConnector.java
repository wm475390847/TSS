package org.sprouts.tss.conner.http;

import org.sprouts.tss.conner.enums.FileContentTypeEnum;
import org.sprouts.tss.conner.exception.ConnerException;
import org.sprouts.tss.conner.utils.FileUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author wangmin
 * @date 2022/6/11 09:43
 */
public class MultipartConnector extends AbstractConnector {
    @Override
    protected void buildRequest(Request.Builder builder, Api api) {
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        api.getFormDataParams().entrySet().stream().filter(e -> e.getKey() != null && e.getValue() != null)
                .forEach(e -> multipartBuilder.addFormDataPart(e.getKey(), e.getValue()));

        Map<String, String> files = api.getFiles();
        if (!files.isEmpty()) {
            files.forEach((key, value) -> {
                String fileName = FileUtils.getLastName(value);
                byte[] fileBytes = FileUtils.getFileBytes(value);
                Optional.ofNullable(fileName).orElseThrow(() -> new ConnerException("fileName is empty"));
                RequestBody requestBody = RequestBody.create(Objects.requireNonNull(MediaType.parse(
                        FileContentTypeEnum.findByFileName(fileName).getContentType())), fileBytes);
                multipartBuilder.addFormDataPart(key, fileName, requestBody);
            });
        }

        // 兼容字符串类型的文件
        List<Api.PartFile> partFiles = api.getPartFiles();
        if (!partFiles.isEmpty()) {
            partFiles.forEach(partFile -> {
                String filename = FileUtils.getLastName(partFile.getFilename());
                byte[] fileBytes = partFile.getContent().getBytes(StandardCharsets.UTF_8);
                RequestBody requestBody = RequestBody.create(Objects.requireNonNull(MediaType.parse(String.valueOf(MultipartBody.FORM))), fileBytes);
                multipartBuilder.addFormDataPart(partFile.getKey(), filename, requestBody);
            });
        }
        builder.post(multipartBuilder.build());
    }
}
