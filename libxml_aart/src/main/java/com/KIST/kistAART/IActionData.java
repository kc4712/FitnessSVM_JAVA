package com.KIST.kistAART;

public interface IActionData
{
    double GetValue(int index);

    void SetFlag(int index, boolean flag);
    double GetFlag(int index);
    void ResetFlag();

    void Reset();
    void ResetSummary();
    void Summary(int index, double val);
    double GetSummary(int index);
    
    void IncCounter(int index);
    double GetCounter(int index);
    void ResetCounter();

    double GetExtend(int index);
    
    void ResetGlobal();
}
