import xbox360_controller
import pygame
import socket
import json

pygame.init()
pygame.joystick.init()
controller = xbox360_controller.Controller(0)
pressed = controller.get_buttons()

HOST = "localhost"  # Change to Raspberry accordingly
PORT = 8091
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.connect((HOST, PORT))

while True:
    # event handling
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            done=True

    # joystick stuff
    pressed = controller.get_buttons()

    a_btn = pressed[xbox360_controller.A]
    b_btn = pressed[xbox360_controller.B]
    x_btn = pressed[xbox360_controller.X]
    y_btn = pressed[xbox360_controller.Y]
    back = pressed[xbox360_controller.BACK]
    start = pressed[xbox360_controller.START]
    # guide = pressed[xbox360_controller.GUIDE]
    lt_bump = pressed[xbox360_controller.LEFT_BUMP]
    rt_bump = pressed[xbox360_controller.RIGHT_BUMP]
    lt_stick_btn = pressed[xbox360_controller.LEFT_STICK_BTN]
    rt_stick_btn = pressed[xbox360_controller.RIGHT_STICK_BTN]

    lt_x, lt_y = controller.get_left_stick()
    rt_x, rt_y = controller.get_right_stick()

    triggers = controller.get_triggers()

    pad_up, pad_right, pad_down, pad_left = controller.get_pad()

    console_data = {"Buttons": {"a_btn": a_btn, "b_btn": b_btn, "x_btn": x_btn, "y_btn": y_btn, "back": back, "start": start},
                    "Bumps and Stick Buttons": {"lt_bump": lt_bump, "rt_bump": rt_bump, "lt_stick_btn": lt_stick_btn, "rt_stick_btn": rt_stick_btn},
                    "Sticks": {"lt_x": lt_x, "lt_y": -lt_y, "rt_x": rt_x, "rt_y": -rt_y},
                    "Triggers": triggers, "Pads": {"pad_up": pad_up, "pad_right": pad_right, "pad_down": pad_down, "pad_left": pad_left}}

    sock.send(bytes(json.dumps(console_data), "utf-8"))
    sock.send(bytes("\n", "utf-8"))




