package kr.co.greencomm.middleware.tool;

import com.KIST.kistAART.IActionData;

import org.w3c.dom.Element;

public abstract class CalcBase extends XClass
    {
        // 공개 속성 및 메서드

        public double Add;
        public double Mul;
        public double Div;

        /// <summary>
        /// 행동 및 확장 데이터를 가져온다.
        /// </summary>
        protected abstract double GetValue(IActionData action);
        /// <summary>
        /// 최종 연산 결과를 반환한다.
        /// </summary>
        public double GetResult(IActionData action)
        {
            double src = GetValue(action);
            double ret = (src + Add) * Mul / Div;
            return ret;
        }

        // 생성자 및 복사
        public CalcBase(Element ele)
        {
        	super(ele);
            Add = Double.parseDouble(ele.getAttribute("Add"));
            Mul = Double.parseDouble(ele.getAttribute("Mul"));
            Div = Double.parseDouble(ele.getAttribute("Div"));
        }
    }
