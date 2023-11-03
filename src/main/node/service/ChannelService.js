const Squeal = require('../model/squeal');
const SquealDestination = require('../model/squealDestination');
const ChannelUser = require('../model/channelUser');
const Channel = require('../model/channel');
const SquealCat = require('../model/squealCat');
const SquealReaction = require('../model/squealReaction');
const SquealViews = require('../model/squealViews');
const User = require('../model/user');
const { isModuleNamespaceObject } = require('util/types');
const channelUserService = require('../service/ChannelUserService');
const accountService = require('../service/AccountService');

class ChannelService {
  async getChannel(user, myUsername, id) {
    let ret = {};
    if (!new accountService().isUserAuthorized(myUsername, user.username)) {
      throw new Error('Unathorized');
    }
    const myUser = await User.findOne({ login: myUsername });
    if (!myUser) {
      throw new Error('username invalid');
    }
    let channel = await Channel.findById(id);
    if (!channel || !channel.type) {
      throw new Error('channel not found or without type');
    }
    switch (channel.type) {
      case 'PRIVATEGROUP':
        if (await new channelUserService().checkSubscribed(channel, myUser)) {
          ret = await this.loadChannelData(channel);
        }
        break;
      case 'PUBLICGROUP':
      case 'MOD':
        ret = await this.loadChannelData(channel);
        break;
      default:
        break;
    }
    return ret;
  }

  async searchChannel(user, myUsername, search) {
    const ret = [];
    if (!new accountService().isUserAuthorized(myUsername, user.username)) {
      throw new Error('Unathorized');
    }

    const myUser = await User.findOne({ login: myUsername });
    if (!myUser) {
      throw new Error('invalid username');
    }
    let channels = await Channel.find({ name: { $regex: '(?i).*' + search + '.*' } });

    for (const ch of channels) {
      switch (ch.type) {
        case 'PRIVATEGROUP':
          if (await new channelUserService().checkSubscribed(ch, myUser)) {
            ret.push(await this.loadChannelData(ch));
          }
          break;
        case 'PUBLICGROUP':
        case 'MOD':
          ret.push(await this.loadChannelData(ch));
          break;
        default:
          break;
      }
    }
    return ret;
  }

  async insertOrUpdateChannel(channel, user, username) {
    const thisUser = await User.findOne({ login: username });
    if (!channel || !thisUser) {
      throw new Error('invalid data');
    }
    if (!new accountService().isUserAuthorized(thisUser._id, user.user_id)) {
      throw new Error('Not authorized');
    }
    if (this.isIncorrectName(channel.name)) {
      throw new Error('Name Invalid');
    }
    if (!channel.name || !channel.type) {
      throw new Error('Incomplete');
    }
    if (channel.type == 'MOD' && !new accountService().isMod(thisUser)) {
      throw new Error('You do not have permission');
    }

    channel.name = this.addPrefix(channel);

    const oldChannel = await Channel.findOne({ name: channel.name });
    if (oldChannel) {
      const userSub = await ChannelUser.findOne({ channel_id: oldChannel._id.toString(), user_id: thisUser._id.toString() });
      if (userSub) {
        throw new Error('You can only have one channel with this name');
      }
    }
    let newChannel = new Channel({
      name: channel.name,
      type: channel.type,
    });

    newChannel = await newChannel.save();
    if (!newChannel) {
      throw new Error('could not save channel');
    }
    const chUser = await ChannelUser.create({
      channel_id: newChannel._id.toString(),
      user_id: thisUser._id.toString(),
      privilege: 'ADMIN',
    });
    if (!chUser) {
      throw new Error('could not create subscription');
    }

    const dto = await this.loadChannelData(newChannel);

    return dto;
  }

  async getChannelSubscribedTo(user, myUsername, search) {
    const ret = [];
    if (!new accountService().isUserAuthorized(myUsername, user.username)) {
      throw new Error('Unathorized');
    }

    const myUser = await User.findOne({ login: myUsername });
    if (!myUser) {
      throw new Error('invalid username');
    }
    const theirUser = await User.findOne({ login: search });
    if (!theirUser) {
      throw new Error('invalid username');
    }

    const chUs = await ChannelUser.find({ user_id: theirUser._id.toString() });
    const chId = [];
    for (const c of chUs) {
      chId.push(c.channel_id);
    }
    let channels = [];
    for (const id of chId) {
      channels.push(await Channel.findById(id));
    }
    for (const ch of channels) {
      switch (ch.type) {
        case 'PRIVATEGROUP':
          if (await new channelUserService().checkSubscribed(ch, myUser)) {
            ret.push(await this.loadChannelData(ch));
          }
          break;
        case 'PUBLICGROUP':
        case 'MOD':
          ret.push(await this.loadChannelData(ch));
          break;
        default:
          break;
      }
    }
    return ret;
  }

  async countChannelSubscribedTo(user, myUsername, search) {
    const subs = await this.getChannelSubscribedTo(user, myUsername, search);
    return subs.length;
  }

  addPrefix(channel) {
    switch (channel.type) {
      case 'MOD':
      case 'PRIVATEGROUP':
        return '§' + channel.name;
      case 'PUBLICGROUP':
        return '#' + channel.name;
    }
  }

  isIncorrectName(q) {
    return q.includes('§') || q.includes('#') || q.includes('@') || q.toLowerCase() !== q;
  }

  async loadChannelData(channel) {
    if (!channel) {
      throw new Error('loading data failed');
    }
    const users = await ChannelUser.find({ channel_id: channel._id.toString() });
    return {
      channel: channel,
      users: users,
    };
  }
}

module.exports = ChannelService;
