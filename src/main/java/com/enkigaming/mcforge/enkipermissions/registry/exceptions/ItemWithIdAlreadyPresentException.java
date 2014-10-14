package com.enkigaming.mcforge.enkipermissions.registry.exceptions;

import java.util.UUID;

public class ItemWithIdAlreadyPresentException extends Exception
{
    public ItemWithIdAlreadyPresentException(Object item, Object itemAlreadyPresent, UUID id)
    {
        this.item = item;
        this.itemAlreadyPresent = itemAlreadyPresent;
        this.id = id;
    }
    
    final Object item;
    final Object itemAlreadyPresent;
    final UUID id;
    
    public Object getItem()
    { return item; }
    
    public Object getItemAlreadyPresent()
    { return itemAlreadyPresent; }
    
    public UUID getId()
    { return id; }
}