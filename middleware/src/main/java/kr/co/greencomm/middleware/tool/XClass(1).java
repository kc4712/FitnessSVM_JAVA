package kr.co.greencomm.middleware.tool;

import org.w3c.dom.Node;

public abstract class XClass
{

	protected void RaiseChange(String propName)
        {
//            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propName));
        }

	// 객체 복사 : ICloneable 구현
	
	public XClass(Node ele) {}
}
