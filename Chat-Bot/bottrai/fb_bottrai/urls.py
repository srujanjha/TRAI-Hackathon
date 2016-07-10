# bottrai/fb_bottrai/urls.py
from django.conf.urls import include, url
from .views import BotTraiView
urlpatterns = [
		url(r'^bf12a71c7bd0475880ce55d5ffae35a8b63f30d6343a67b243/?$', BotTraiView.as_view())
]