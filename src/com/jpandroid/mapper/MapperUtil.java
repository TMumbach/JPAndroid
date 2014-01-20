package com.jpandroid.mapper;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.widget.EditText;

import com.jpandroid.annotations.FieldMapper;
import com.jpandroid.entity.Entity;

public class MapperUtil
{

	@SuppressWarnings("all")
	public static boolean processFields(Entity currentEntity, Object object)
	{
		Field[] declaredFields = object.getClass().getDeclaredFields();
		try
		{
			for (Field field : declaredFields)
			{
				FieldMapper annotation = field.getAnnotation(FieldMapper.class);

				if (annotation != null)
				{

					field.setAccessible(true);

					EditText editText = (EditText) field.get(object);

					if (annotation.required() && editText.getText().toString().equals(""))
					{
						return false;
					}

					if (editText.getText().toString().equals(""))
					{
						continue;
					}

					Field newValue = currentEntity.getClass().getDeclaredField(annotation.fieldName());

					newValue.setAccessible(true);

					if (annotation.fieldType().equals(Integer.class))
					{
						newValue.set(currentEntity, Integer.valueOf(editText.getText().toString()));
					}

					else if (annotation.fieldType().equals(String.class))
					{
						newValue.set(currentEntity, editText.getText().toString());
					}

					else if (annotation.fieldType().equals(Float.class))
					{
						newValue.set(currentEntity, Float.valueOf(editText.getText().toString()));
					}

					else if (annotation.fieldType().equals(Character.class))
					{
						newValue.set(currentEntity, new Character(editText.getText().toString().charAt(0)));
					}

					else if (annotation.fieldType().equals(Date.class))
					{
						SimpleDateFormat formatter = new SimpleDateFormat(annotation.dateFormat());
						newValue.set(currentEntity, formatter.parse(editText.getText().toString()));
					}
				}
			}
		} catch (Exception e1)
		{
			e1.printStackTrace();
		}
		return true;
	}

	public static boolean checkRequiredFields(Activity activity)
	{
		Field[] declaredFields = activity.getClass().getDeclaredFields();
		try
		{
			for (Field field : declaredFields)
			{
				FieldMapper annotation = field.getAnnotation(FieldMapper.class);

				if (annotation != null)
				{
					try
					{
						field.setAccessible(true);
						EditText editText = (EditText) field.get(activity);

						if (annotation.required() && editText.getText().equals(""))
						{
							return false;
						}

					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e1)
		{
			e1.printStackTrace();
		}
		return true;
	}

	public static boolean checkRequiredFields(EditText... editTexts)
	{
		try
		{
			for (EditText et : editTexts)
			{
				FieldMapper annotation = et.getClass().getAnnotation(FieldMapper.class);

				if (annotation != null)
				{
					try
					{
						if (annotation.required() && et.getText().equals(""))
						{
							return false;
						}

					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e1)
		{
			e1.printStackTrace();
		}
		return true;
	}
}
