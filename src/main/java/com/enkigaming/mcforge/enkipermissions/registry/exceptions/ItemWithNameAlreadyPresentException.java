package com.enkigaming.mcforge.enkipermissions.registry.exceptions;

public class ItemWithNameAlreadyPresentException extends Exception
{
    public ItemWithNameAlreadyPresentException(Object item, Object itemAlreadyPresent, String name)
    {
        this.item = item;
        this.itemAlreadyPresent = itemAlreadyPresent;
        this.name = name;
    }
    
    final Object item;
    final Object itemAlreadyPresent;
    final String name;
    
    public Object getItem()
    { return item; }
    
    public Object getItemAlreadyPresent()
    { return itemAlreadyPresent; }
    
    public String getName()
    { return name; }
}