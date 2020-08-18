package kr.co.greencomm.middleware.tool;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.KIST.kistAART.IActionData;

/// <summary>
    /// 계산식 합산 블럭
    /// 하위의 계산식의 계산 결과를 모두 합산한다.
    /// </summary>
	public class CalcBlock extends CalcBase
    {
        // 공개 속성 및 메서드
        public ArrayList<CalcItem> List;
        public boolean Multiply = false;
    	public boolean Abs = false;

        /// <summary>
        /// 행동 및 확장 데이터를 가져온다.
        /// </summary>
        protected double GetValue(IActionData action)
        {
            double ret = Multiply ? 1 : 0;
            for (CalcItem calcItem : List) {
            	if(Multiply) {
             		ret *= calcItem.GetResult(action);
            	} else {
            		ret += calcItem.GetResult(action);	
            	}
			}
            
            if(Abs) {
            	ret = Math.abs(ret);
            }
            
            return ret;
        }

        // 생성자 및 복사
        public CalcBlock(Element ele)
        {
        	super(ele);
        	Add = Double.parseDouble(ele.getAttribute("Add"));
            Mul = Double.parseDouble(ele.getAttribute("Mul"));
            Div = Double.parseDouble(ele.getAttribute("Div"));
            Multiply = Boolean.parseBoolean(ele.getAttribute("Multiply"));
            Abs = Boolean.parseBoolean(ele.getAttribute("Abs"));
            
            List = new ArrayList<CalcItem>();
            NodeList ns = ele.getElementsByTagName("CalcItem");
            for(int i=0; i < ns.getLength(); i++) {
            	List.add(new CalcItem((Element)ns.item(i)));
            }
        }
    }

