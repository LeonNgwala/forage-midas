package com.jpmc.midascore.entity;                                                                     
                                                                                                       
import jakarta.persistence.*;                                                                          
import java.math.BigDecimal;                                                                           
import java.time.LocalDateTime;                                                                        
                                                                                                        
@Entity                                                                                                
@Table(name = "transactions") // specifies the table name in the database                    
public class TransactionRecord {                                                                       
                                                                                                       
    @Id                                                                                                
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrementing primary key               
    private Long id;                                                                                   
                                                                                                   
    @ManyToOne(fetch = FetchType.LAZY) // Many transactions to one sender                              
    @JoinColumn(name = "sender_id", nullable = false) // Foreign key column                            
    private UserRecord sender;                                                                         
                                                                                                     
    @ManyToOne(fetch = FetchType.LAZY) // Many transactions to one recipient
    @JoinColumn(name = "recipient_id", nullable = false) // Foreign key column
    private UserRecord recipient;

    @Column(nullable = false)
    private BigDecimal amount;
                                                                                                     
    @Column(nullable = false)                                                                          
    private LocalDateTime timestamp;                                                                   
                                                                                                        
    // Default constructor for JPA                                                                     
    public TransactionRecord() {                                                                       
    }

    public TransactionRecord(UserRecord sender, UserRecord recipient, BigDecimal amount, LocalDateTime 
    timestamp) {                                                                                           
    this.sender = sender;                                                                          
    this.recipient = recipient;                                                                    
    this.amount = amount;                                                                          
    this.timestamp = timestamp;                                                                    
    }                                                                                                  
                                                                                                      
    // Getters and Setters                                                                             
    public Long getId() {                                                                              
    return id;                                                                                     
    }                                                                                                  
                                                                                                        
    public void setId(Long id) {                                                                       
    this.id = id;                                                                                  
    }                                                                                                  
                                                                                                        
    public UserRecord getSender() {                                                                    
    return sender;                                                                                 
    }                                                                                                  
                                                                                                       
    public void setSender(UserRecord sender) {                                                         
    this.sender = sender;                                                                          
    }                                                                                                  
                                                                                                        
   public UserRecord getRecipient() {                                                                 
   return recipient;                                                                              
   }                                                                                                  
                                                                                                       
   public void setRecipient(UserRecord recipient) {                                                   
   this.recipient = recipient;                                                                    
   }                                                                                                  
                                                                                                        
   public BigDecimal getAmount() {                                                                    
   return amount;                                                                                 
   }                                                                          

   public void setAmount(BigDecimal amount) {                                      
   this.amount = amount;                                                                    
   }                                                
   public LocalDateTime getTimestamp() {                                                              
   return timestamp;                                                                              
   }

   public void setTimestamp(LocalDateTime timestamp) {                                                
   this.timestamp = timestamp;                                                                    
   }                                                                                                  
                                                                                                        
   @Override                                                                                          
   public String toString() {                                                                         
   return "TransactionRecord{" + 
   "id=" + id + 
   ", sender=" + (sender != null ? sender.getId() : "null") + 
   ", recipient=" + (recipient != null ? recipient.getId() : "null") + 
   ", amount=" + amount + 
   ", timestamp=" + timestamp +
   '}';                                                                                    
   }                                                                                                  
}                               