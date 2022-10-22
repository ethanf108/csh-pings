import asyncio
import os
import select
import sys
import threading
import time

import discord
from discord.ext import tasks

TOKEN = sys.argv[1]
client = discord.Client(intents=discord.Intents(
    (1 << 0)  # GUILDS
    | (1 << 1)  # GUILD_MEMBERS
    # | (1 << 2) # GUILD_BANS
    # | (1 << 3) # GUILD_EMOJIS
    # | (1 << 4) # GUILD_INTEGRATIONS
    # | (1 << 5) # GUILD_WEBHOOKS
    # | (1 << 6) # GUILD_INVITES
    # | (1 << 7) # GUILD_VOICE_STATES
    # | (1 << 8) # GUILD_PRESENCES
    | (1 << 9)  # GUILD_MESSAGES
    # | (1 << 10) # GUILD_MESSAGE_REACTIONS
    # | (1 << 11) # GUILD_MESSAGE_TYPING
    # | (1 << 12) # DIRECT_MESSAGES
    # | (1 << 13) # DIRECT_MESSAGE_REACTIONS
    # | (1 << 14) # DIRECT_MESSAGE_TYPING
    # | (1 << 15) # MESSAGE_CONTENT
    # | (1 << 16) # GUILD_SCHEDULED_EVENTS
    # | (1 << 20) # AUTO_MODERATION_CONFIGURATION
    # | (1 << 21) # AUTO_MODERATION_EXECUTION
))
userlist = []

data = []


@client.event
async def on_ready():
    print(f'Logged in as {client.user}', flush=True)
    handle_input.start()
    # await send_message(client.get_user(get_user_id('Creeper', '3621')), 'Initialized successfully!')


# send a message to a user
async def send_message(user, message):
    await user.send(message)
    print(f'Sent message to {user.name}#{user.discriminator}', flush=True)


# get a user id from a username and discriminator
def get_user_id(username, discriminator):
    for user in client.users:
        if user.name == username and user.discriminator == discriminator:
            return user.id
    return None


@tasks.loop(seconds=0.5, reconnect=True)
async def handle_input():
    # stdin must be submissive and readable
    if sys.stdin in select.select([sys.stdin], [], [], 0)[0]:
        print("stdin has data", flush=True)
        line = sys.stdin.readline()
    else:
        print("stdin has no data", flush=True)
        return
    # print(f'echo: {line.strip()}', flush=True)
    line = line.strip()
    args = line.split(' ')
    if args[0] == 'send':
        if len(args) < 4:
            print(f'Invalid arguments for send: {args}')
        else:
            user = client.get_user(get_user_id(args[1], args[2]))
            if user is None:
                print(f'User {args[1]}#{args[2]} not found')
            else:
                await send_message(user, ' '.join(args[3:]))
    if args[0] == 'list':
        for user in client.users:
            print(f'{user.name}#{user.discriminator}')
    if args[0] == 'ping':
        print('pong')
    if args[0] == 'exit':
        await client.close()
        sys.exit(0)
    # flush stdout
    sys.stdout.flush()

@handle_input.before_loop
async def before_handle_stdin():
    await client.wait_until_ready()


if __name__ == '__main__':
    client.run(TOKEN)
