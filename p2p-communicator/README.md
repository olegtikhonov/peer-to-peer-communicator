## This document will track the approach of creating own crypto

#### Steps are
* Create a secure group - this group like a kernel, every node will be trying to connect to it.
* Create a peer node - it is an entity of the peer to peer network.
* Choose a consensus mechanism - for example: Proof Of Work. I'm going to check Zero Trust Proof.


#### Thoughts

1. Currently, every existing crypto based ob bitcoin network, which is distributed but not decentralized.
I do not like a concept of powerful center which controls every aspect of crypto.
As result, this project solves a problem of powerful center. With it you may create your own network and control by 
yourself everything. Your world, your rules.

ZKP - Zero Knowledge Proof
zk SNARK
Mina protocol
Pinocchio protocol
BTC 
ETC
IOTA
Stellar
DeFi protocols
transaction batching
segwit
bech32
bitcoind
geth
openethereum

####
Merkle tree
Consensus in Mina Ouroboros Samasika
parallel scan state
five PPT algorithms (VerifyConsensus, UpdateConsensus, VerifyBlock, UpdateChain, VerifyChain)
DeFi projects like Uniswap, Nexus Mutual, Opyn and VouchForMe, PoolTogether, Dharma and Argent
Decentralised Exchange (DEX)
new blockchain networks like Solana, Cardano, Polkadot

(DeFi protocols)(https://www.moneycontrol.com/msite/wazirx-cryptocontrol-articles/how-defi-is-revolutionizing-the-financial-industry/)
* Aave - lending protocol
* yEarn - native token YFI
* Synthetix - naitive token, SNX
* Compound - lending protocol, native token, COMP
* Uniswap - decentralized exchange (DEX), native token UNI
* Kyber Network - another DEX. Native token KNC - Kyber Network Crystals
* Sushiswap - a top AMM (automated market-making)



#### References

https://docs.minaprotocol.com/static/pdf/technicalWhitepaper.pdf

https://101blockchains.com/stablecoins/

[Simple example of blockchain](https://www.baeldung.com/java-blockchain)

[Lightweight consensys protocol for IoT](https://arxiv.org/pdf/2005.09443.pdf)


#### How blockchain looks like
```

                             |===============|
                             |   Timestamp   | 
                             |===============|
                                  \/
|===============|            |===============|           |===============| 
| Previous Hash |    ---->   | Transactions  |    ---->  |  Current Hash |
|===============|            |===============|           |===============|
                                  /\
                             |===============|
                             |     Nonce     |
                             |===============| 

```

#### Consensus Mechanism
Note: Mostly all modern cryptocurrencies us "Proof Of Work" which contains two parts: i. Calculation of the hashcode ii.
Verification of the hash
Drawback: Very expensive. Requires a lot of resources. Now energy efficient.



#### Summary what we have so far
1. We can create completely independent peer to peer network. Unlike others, it is not based on any existing network.
2. Continue to find the best energy efficient Consensus Mechanism.
   When we find it, we could create our own tokens/coins.

##### From another document
## My crypto Coins - ocrix

#### Benefits
* Eliminate Fraud Risk
* Transaction anonymity
* Lower Operational Costs
* Immediate Transactions
* Access to New Customer Base
* Security For Funds

#### Step 1: choose consensus mechanism
* Sybil attacks are when one user or group pretends to be many users.
  The combination of proof-of-work and longest chain rule is known as "Nakamoto Consensus."
* Casper the Friendly Finality Gadget
  The protocol already implemented in Ethereum, the github repo as https://github.com/ethereum/casper/tree/master/casper/contract.

Hyperledger consensus mechanism - from Linux foundation

**Interesting facts about currencies**
Basically serve two main purposes: as a medium of exchange and a store of value.

1. Create peer to peer network
   I do not want to be monitored by cloud providers
   I do want to control my peers, allow to connect or not - optional
   EmbJXTAChord, a novel peer-to-peer (P2P) architecture

EmbJXTAChord provides for several interesting properties, such as, distributed and fault-tolerant resource discovery, transparent routing over subnetworks, applica- tion protocol independence from the transport protocol in narrowband Wireless Sensor Networks, thus eliminating the need for using dedicated software or configuring custom gateways to achieve these functionalities.

EmbJXTAChord offers native support not only for TCP/HTTP, but also for Bluetooth RFCOMM and 6LoWPAN
Under CoAP, a new protocol recently proposed by the IETF CoRE Working Group, all the functionalities are seen as a set of resources, identified by a URI (Uniform Resource Identifier) and all the operations are performed through four methods (GET, PUT, POST and DELETE).

* distributed service discovery
* fault tolerance
* resource protection
  Bluetooth Low Energy (BLE)
  Chord is an algorithm based on theory of consistent hashing.

Attacks:
* Proportional Reward Scheme
* Expected Reward
  Note: if you solo mining you will reward: 0.128 btc/year
* Forking & Double Spends
* Race Attack
* Feather Forking


Goals:
Energy efficient
1. Proof of Authority (PoA): a consensus algorithm that does not require any mining activity
2. Hyperledger: a private and permissioned blockchain or in other words, a centralized or semi-
   centralized model. In this type of blockchain, it is possible to allow access and permissions just to a
   group of participants
3. Zero Knowledge Proof  
   **Proof Of State** – PoS: In this consensus algorithm each node that wants to participate in the creation
   of a block will have to deposit an amount as insurance that he will “play by the rules”. If a node fails
   to do so and compromises the consistency of the blockchain, the deposit is lost. This way each node
   that creates blocks has a “stake” in the success of the blockchain. The higher the deposit, the higher
   the incentive to ensure the blockchain works as expected.  
   The consensus algorithm selects randomly which node will create each new block taking into account
   the stake it has in the system. Once selected, the node simply validates the state changes and creates
   the block without the need to do any additional work as in PoW. The protocol then requires additional
   validation for the network nodes before accepting the block in the blockchain. In a PoS blockchain
   the nodes that create blocks are referred to as “Validators” or as “Forgers” and the block creation is
   referred to as “Minting”.


**Proof Of Authority** – PoA: This is similar to the PoS consensus algorithm with the difference that
in order to become a Validator one needs to be accepted by a centralized authority and not a stake on
the system. This approach minimizes the energy demands of a blockchain.

**Proof  of  Elapsed  Time**  –  PoET:  A  consensus  algorithm  that  requires participants’ identification,
which  means  that  it  is  more  common  in  a  permissioned  style  blockchain  than  a  public  one  due  to
efficiency reasons. PoET prevents high resource utilization, energy consumption and
operational efficiency.

Secured
References:
[Good explanation of different types of Consensus algorithm](https://www.itu.int/en/ITU-T/focusgroups/ai4ee/Documents/TS-D.WG2_05-Guidelines%20on%20Energy%20Efficient%20Blockchain%20Systems_Anthopoulos_Nikolau.pdf)
[p2p emb chord protocol](https://arxiv.org/pdf/1711.08218.pdf)
[Blockchain at Berkeley](https://www.youtube.com/watch?v=bMIMkMEM0-Q&list=RDCMUC5sgoRfoSp3jeX4DEqKLwKg&index=13)



Why I need peer to peer network for myself?
* Example recent problems in Kazakhstan, where is part of my family is living.
  When everything started the first thing is done, shut down the internet. No whatsup, no phones.
  However, Skype was working !!! Because it is very complicated to destroy de-centralized distributed system.
