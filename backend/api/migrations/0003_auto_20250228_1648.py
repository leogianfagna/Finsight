# Generated by Django 3.1.12 on 2025-02-28 19:48

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('api', '0002_auto_20250228_1638'),
    ]

    operations = [
        migrations.AlterField(
            model_name='users',
            name='user',
            field=models.CharField(max_length=100),
        ),
    ]
