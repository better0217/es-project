// 文件路径: src/main/java/com/example/esproject/converter/WorkOrderConverter.java
package com.example.esproject.converter;

import com.example.esproject.document.WorkOrderDocument;
import com.example.esproject.domain.CnsCinfo;
import org.springframework.stereotype.Component;

@Component
public class WorkOrderConverter {

    public WorkOrderDocument toDocument(CnsCinfo cnsCinfo) {
        if (cnsCinfo == null) {
            return null;
        }

        return WorkOrderDocument.builder()
                                .rowGuid(cnsCinfo.getRowGuid())
                                .serialNum(cnsCinfo.getSerialNum())
                                .rqstPerson(cnsCinfo.getRqstPerson())
                                .rqstTime(cnsCinfo.getRqstTime())
                                .rqstNumber(cnsCinfo.getRqstNumber())
                                .linkNumber(cnsCinfo.getLinkNumber())
                                .rqstAreaCode(cnsCinfo.getRqstAreaCode())
                                .rqstAddress(cnsCinfo.getRqstAddress())
                                .rqstType(cnsCinfo.getRqstType())
                                .accordType(cnsCinfo.getAccordType())
                                .rqstTitle(cnsCinfo.getRqstTitle())
                                .rqstContent(cnsCinfo.getRqstContent())
                                .rqstSource(cnsCinfo.getRqstSource())
                                .registerName(cnsCinfo.getRegisterName())
                                .cStatus(cnsCinfo.getCStatus())
                                .isImpt(cnsCinfo.getIsImpt())
                                .finishTime(cnsCinfo.getFinishTime())
                                // TODO: 在真实项目中, 您可以在这里通过cnsCinfo的其他ID去查询关联数据,
                                //  比如通话文本, 然后设置到telTextContent字段, 实现数据的反规范化。
                                //  .telTextContent(findTextByGuid(cnsCinfo.getRowGuid()))
                                .build();
    }
}