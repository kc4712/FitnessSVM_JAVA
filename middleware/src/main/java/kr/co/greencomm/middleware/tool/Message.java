package kr.co.greencomm.middleware.tool;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

public class Message extends XClass {
    // ���� �Ӽ� �� �޼���

    public int Code;
    public ArrayList<Translation> List;

    // ������ �� ����

    public Message(Element ele) {
        super(ele);
        Code = Integer.parseInt(ele.getAttribute("Code"));

        List = new ArrayList<>();
        NodeList ns = ele.getElementsByTagName("Translation");
        for (int i = 0; i < ns.getLength(); i++) {
            List.add(new Translation((Element) ns.item(i)));
        }
    }
}
