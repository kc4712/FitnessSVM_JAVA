package kr.co.greencomm.middleware.tool;

import org.w3c.dom.Element;

public class Translation extends XClass {
    // ���� �Ӽ� �� �޼���

    public String Language;
    public String Display;

    // ������ �� ����

    public Translation(Element ele) {
        super(ele);
        Language = ele.getAttribute("Language");
        Display = ele.getAttribute("Display");
    }
}
